package org.nerd.kid.model;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.FileUtils;
import org.nerd.kid.arff.ArffParser;
import org.nerd.kid.evaluation.ModelEvaluation;
import org.nerd.kid.service.NerdKidPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.classification.RandomForest;
import smile.data.AttributeDataset;
import smile.math.Math;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/*
class to build machine learning models from datasets using Random Forests
* */

public class ModelBuilder {
    private ModelEvaluation evaluation;
    private ArffParser accessArff;
    private XStream streamer;
    private smile.data.parser.ArffParser arffParser;
    private AttributeDataset attributeDataset = null;
    private RandomForest forest = null;
    private static final Logger logger = LoggerFactory.getLogger(ModelBuilder.class);

    public ModelBuilder(){
        evaluation = new ModelEvaluation();
        accessArff = new ArffParser();
        streamer = new XStream();
        arffParser = new smile.data.parser.ArffParser();
    }

    public void loadData(File file) throws Exception {
        // parsing the initial file to get the response index
        attributeDataset = arffParser.parse(new FileInputStream(file));

        // getting the response index
        int responseIndex = attributeDataset.attributes().length - 1;

        loadData(new FileInputStream(file), responseIndex);

        // information about the file
        System.out.println("Loading training data " + file.getPath() + " is finished successfully.");
    }

    // load the model, if there is only training data
    public void loadData(InputStream file, int responseIndex) throws Exception {
        // setResponseIndex is response variable; for classification, it is the class label; for regression, it is of real value
        arffParser = new smile.data.parser.ArffParser().setResponseIndex(responseIndex);

        // parsing the file to get the dataset
        attributeDataset = arffParser.parse(file);
    }

    // splitting the model into training and data set
    public void splitModel(int split) throws Exception {
        String pathOutput = NerdKidPaths.RESULT_TXT + "/Result_Trained_Model.txt";
        // if there isn't any training data
        if (attributeDataset == null) {
            logger.debug("Training data doesn't exist");
        }

        // training the data
        logger.info("Training the model.");

        // datax is for the examples, datay is for the class
        double[][] datax = attributeDataset.toArray(new double[attributeDataset.size()][]);
        int[] datay = attributeDataset.toArray(new int[attributeDataset.size()]);

        int max = 0;
        // finding the biggest index in datay
        for (int i = 1; i < datay.length; i++) {
            if (datay[i] > max) {
                max = datay[i];
            }
        }

        // size of examples
        int n = attributeDataset.size();

        // size of examples after split in certain percentage
        int m = n * split / 100;
        int[] index = Math.permutate(n); // to get the index of the row randomly

        // training data after splitting in certain percentage
        double[][] trainx = new double[m][];
        int[] trainy = new int[m];
        for (int i = 0; i < m; i++) {
            trainx[i] = datax[index[i]];
            trainy[i] = datay[index[i]];
        }

        // testing data after splitting in certain percentage
        double[][] testx = new double[n - m][];
        int[] testy = new int[n - m];
        for (int i = m; i < n; i++) {
            testx[i - m] = datax[index[i]];
            testy[i - m] = datay[index[i]];
        }

        // training with Random Forest classification
        forest = new RandomForest(attributeDataset.attributes(), trainx, trainy, 100);

        // printing the result
        outputResults(System.out, testx, testy, max);

        // creating text file from the result
        outputResults(new PrintStream(new FileOutputStream(pathOutput)), testx, testy, max);
    }

    public int[] predictTestData(double[][] Testx) {
        int[] yPredict = new int[Testx.length];
        // predicting the test
        for (int i = 0; i < Testx.length; i++) {
            yPredict[i] = forest.predict(Testx[i]);
        }
        return yPredict;
    }

    public void outputResults(PrintStream output, double[][] Testx, int[] Testy, int max) throws Exception {
        String pathFileInput = NerdKidPaths.RESULT_ARFF + "/Training.arff";
        File fileInput = new File(pathFileInput);
        // prediction and calculating the classes classified
        int[] yPredict = predictTestData(Testx);

        // counting class classified or not
        int count_error = evaluation.countingInstanceNotClassified(Testy, yPredict);
        int count_classified = evaluation.countingInstanceClassified(Testy, yPredict);

        // total instances
        double total_instances = count_error + count_classified;

        // size of data
        int sizeDataAll = attributeDataset.size();
        int sizeDataTrained = sizeDataAll - (int) total_instances;
        int sizeDataPredicted = (int) total_instances;

        // element of class i
        String[] dataClass = accessArff.readClassArff(fileInput);

        // calling the method of confusion matrix
        int[][] confusMatrix = evaluation.confusionMatrix(Testy, yPredict, max);
        int[] TP = evaluation.countingTruePositive(confusMatrix, max);
        int[] TN = evaluation.countingTrueNegative(confusMatrix, max);
        int[] FP = evaluation.countingFalsePositive(confusMatrix, max);
        int[] FN = evaluation.countingFalseNegative(confusMatrix, max);
        int totalAll = evaluation.countingTotalClass(confusMatrix, max);
        double[] resultAccuracy = evaluation.accuracy(TP, TN, FP, FN);
        double[] resultPrecision = evaluation.precision(TP, FP);
        double[] resultRecall = evaluation.recall(TP, FN);
        double[] resultSpecificity = evaluation.specificity(TN, FP);
        double[] resultFmeasure = evaluation.fmeasure(resultPrecision, resultRecall);

        // classfied instances
        output.println("** Classification with Random Forest of " + forest.size() + " trees **");
        output.print("\n");
        output.format("Total of instances\t\t\t\t\t:\t %d \n", sizeDataAll);
        output.format("Number of instance trained\t\t\t:\t %d \n", sizeDataTrained);
        output.format("Number of instance predicted\t\t:\t %d \n", sizeDataPredicted);
        output.format("Correctly classified instances\t\t:\t %d (%.3f %%) %n", count_classified, count_classified / total_instances * 100.00);
        output.format("Incorrectly classified instances\t:\t %d (%.3f %%) %n", count_error, count_error / total_instances * 100.00);
        output.format("Out of Bag (OOB) error rate\t\t\t:\t %.3f%n", forest.error());
        output.format("Specificity\t\t\t\t\t\t\t:\t %.3f %n", evaluation.averageSpecificity(resultSpecificity));
        output.format("Average of accuracy\t\t\t\t\t:\t %.3f%n", evaluation.averageAccuracy(resultAccuracy));
        // FMeasure, Precision, Recall for all classes
        output.format("Macro Average of Precision\t\t\t:\t %.3f%n", evaluation.averagePrecisionMacro(resultPrecision));
        output.format("Micro Average of Precision\t\t\t:\t %.3f%n", evaluation.averagePrecisionMicro(TP, FP));
        output.format("Macro Average of Recall\t\t\t\t:\t %.3f%n", evaluation.averageRecallMacro(resultRecall));
        output.format("Micro Average of Recall\t\t\t\t:\t %.3f%n", evaluation.averageRecallMicro(TP, FN));
        output.format("Macro Average of FMeasure\t\t\t:\t %.3f%n", evaluation.averageFmeasure(evaluation.averagePrecisionMacro(resultPrecision), evaluation.averageRecallMacro(resultRecall)));
        output.format("Micro Average of FMeasure\t\t\t:\t %.3f%n", evaluation.averageFmeasure(evaluation.averagePrecisionMicro(TP, FP), evaluation.averageRecallMicro(TP, FN)));

        output.println("\n** Confusion Matrix **");
        output.println("Row: Actual; Column: Predicted");
        output.println("Label of class:");
        output.print("{");
        for (int i = 0; i <= max; i++) {
            output.print(i + ":" + dataClass[i]);
            if (i != max)
                output.print("; ");
        }
        output.print("}\n\n");

        output.print("Class:\t");
        for (int i = 0; i <= max; i++) {
            output.print(i + "\t");
        }
        output.print("\n");
        for (int i = 0; i <= max; i++) {
            output.print("\t" + i + "|\t");
            for (int j = 0; j <= max; j++) {
                output.print(confusMatrix[i][j] + "\t");
            }
            output.print("\n");
        }

        // FMeasure, Precision, Recall, accuracy for every class
        output.println("\n** Validation for each class **");
        output.printf("\nClass\t\t:");
        for (int i = 0; i <= max; i++) {
            output.printf("\t" + i + "\t");
        }

        output.printf("\nAccuracy\t:");
        for (int i = 0; i <= max; i++) {
            output.format("\t%.2f", resultAccuracy[i]);
        }

        output.printf("\nPrecision\t:");
        for (int i = 0; i <= max; i++) {
            output.format("\t%.2f", resultPrecision[i]);
        }

        output.printf("\nRecall\t\t:");
        for (int i = 0; i <= max; i++) {
            output.format("\t%.2f", resultRecall[i]);
        }

        output.printf("\nFMeasure\t:");
        for (int i = 0; i <= max; i++) {
            output.format("\t%.2f", resultFmeasure[i]);
        }

        output.printf("\nSpecificity\t:");
        for (int i = 0; i <= max; i++) {
            output.format("\t%.2f", resultSpecificity[i]);
        }

        output.println("\n");
    }

    // method to save the model built
    public void saveModelToXML(File modelFile) {
        try {
            if (forest == null) {
                throw new RuntimeException("No model exists.");
            }

            if (modelFile.exists()) {
                String renameTo = modelFile.getAbsoluteFile() + ".old";
                modelFile.renameTo(new File(renameTo));
                FileUtils.deleteQuietly(modelFile);
            }
            streamer.toXML(this.forest, new FileOutputStream(modelFile));
        }catch (FileNotFoundException e){
            e.printStackTrace();
            System.out.println("Error : " + e.getMessage());
        }
    }

    // create zip file from the model built
    public void createZip(byte[] inputByte, File outputFile) throws IOException {
        GZIPOutputStream gzipOutputStream = null;
        FileOutputStream outputStream = null;
        try{
            if (forest == null){
                throw  new RuntimeException("No model exists.");
            }

            if (outputFile.exists()){
                String renameTo = outputFile.getAbsoluteFile() + ".old";
                outputFile.renameTo(new File(renameTo));
                FileUtils.deleteQuietly(outputFile);
            }
            outputStream = new FileOutputStream(outputFile);
            gzipOutputStream = new GZIPOutputStream(outputStream);

            byte[] data = inputByte;
            gzipOutputStream.write(data);

        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Error : " + e.getMessage());
        }finally {
            gzipOutputStream.close();
            outputStream.close();
        }
    }

    // read bytes from file
    public byte[] readBytesFromFile(File inputFile){
        FileInputStream fileInputStream = null;
        byte[] bytes = null;
        try{
            bytes = new byte[(int) inputFile.length()];
            fileInputStream = new FileInputStream(inputFile);
            fileInputStream.read(bytes);
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Error : " + e.getMessage());
        }
        return bytes;
    }

    public InputStream readZipFile(InputStream is) {
        GZIPInputStream gzipInputStream = null;
        try {
            gzipInputStream = new GZIPInputStream(is);

        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Error : " + e.getMessage());
        }
        return gzipInputStream;
    }

    /*main class to build a model from training file*/
    public static void main(String[] args) throws Exception {
        final String pathnameXML = "/tmp/model.xml";
        final String pathnameZIP = "/tmp/model.zip";
        String fileInput = "Training.arff";
        String fileOutput = "Result_Trained_Model.txt";
        String pathInput = NerdKidPaths.RESULT_ARFF + "/" + fileInput;

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.loadData(new File(pathInput));

        int split = 80;
        System.out.print("Percentage of training data (in %): " + split);
        modelBuilder.splitModel(split);
        System.out.println("Result can be found in " + NerdKidPaths.RESULT_TXT + "/" + fileOutput);

        modelBuilder.saveModelToXML(new File(pathnameXML));
        byte[] resultInBytes= modelBuilder.readBytesFromFile(new File(pathnameXML));
        modelBuilder.createZip(resultInBytes, new File(pathnameZIP));
        System.out.println("Model has been saved in " + pathnameXML + " and " + pathnameZIP);
    }
}
