package org.nerd.kid.main;

import org.nerd.kid.arff.AccessArff;
import org.nerd.kid.evaluation.ModelEvaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smile.data.AttributeDataset;
import smile.data.parser.ArffParser;
import smile.classification.RandomForest;
import smile.math.Math;

import java.io.*;
import java.lang.*;

/* this class contains the use of Smile Java API for parsing the model */
public class KidService {
    // creating objects
    ModelEvaluation evaluation = new ModelEvaluation();
    AccessArff accessArff = new AccessArff();

    // --------------- attributes ---------------
    private ArffParser arffParser = new ArffParser();
    private AttributeDataset attributeDataset = null;
    private AttributeDataset training = null;
    private AttributeDataset testing = null;

    //classification model of Random Forest
    private RandomForest forest = null;

    //logger
    private static final Logger logger = LoggerFactory.getLogger(KidService.class);

    // --------------- methods ---------------

    public void loadData(File file) throws Exception {
        // parsing the initial file to get the response index
        attributeDataset = arffParser.parse(new FileInputStream(file));

        // getting the response index
        int responseIndex = attributeDataset.attributes().length - 1;

        loadData(new FileInputStream(file), responseIndex);

        // information about the file
        System.out.println("Loading training data " + file.getPath() + " is finished successfully.");
    }

    // loading the model, if there is only the training data
    public void loadData(InputStream file, int responseIndex) throws Exception {
        // setResponseIndex is response variable; for classification, it is the class label; for regression, it is of real value
        arffParser = new ArffParser().setResponseIndex(responseIndex);

        // parsing the file to get the dataset
        attributeDataset = arffParser.parse(file);
    }

    // validation using training and testing data
    public void trainTestModel(File fileInput, File fileOutput) throws Exception {
        // if there isn't any training data
        if (training == null) {
            logger.debug("Training data doesn't exist");
        }

        // if there isn't any training data
        if (testing == null) {
            logger.debug("Testing data doesn't exist");
        }

        // training the data
        logger.info("Training the model.");

        // for getting output stream of the file for writing the result
        File fl = new File("result/Result_" + fileOutput.getName() + ".txt");

        BufferedWriter result = new BufferedWriter(new FileWriter(fl));

        // datax is for the examples, datay is for the class
        double[][] trainx = training.toArray(new double[training.size()][]);
        int[] trainy = training.toArray(new int[training.size()]);
        double[][] testx = testing.toArray(new double[testing.size()][]);
        int[] testy = testing.toArray(new int[testing.size()]);

        int maxTemp = trainy[0];
        // finding the biggest index in trainy
        for (int i = 1; i < trainy.length; i++) {
            if (trainy[i] >= maxTemp) {
                maxTemp = trainy[i];
            }
        }
        int max = maxTemp;
        // finding the biggest index in testy
        for (int i = 0; i < testy.length; i++) {
            if (testy[i] >= max) {
                max = testy[i];
            }
        }

        // finding the biggest index between trainy and testy
        if (maxTemp >= max)
            max = maxTemp;

        // training with Random Forest classification
        forest = new RandomForest(attributeDataset.attributes(), trainx, trainy, 100);

        // printing the result
        outputResults(fileInput, System.out, testx, testy, max);

        // creating text file from the result
        outputResults(fileInput, new PrintStream(new FileOutputStream("result/Result_" + fileOutput + ".txt")), testx, testy, max);
    }

    // splitting the model into training and data set
    public void splitModel(File fileInput, File fileOutput, int split) throws Exception {
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
        outputResults(fileInput, System.out, testx, testy, max);

        // creating text file from the result
        outputResults(fileInput, new PrintStream(new FileOutputStream("result/Result_" + fileOutput + ".txt")), testx, testy, max);
    }

    public int[] predictTestData(double[][] Testx) {
        int[] yPredict = new int[Testx.length];
        // predicting the test
        for (int i = 0; i < Testx.length; i++) {
            yPredict[i] = forest.predict(Testx[i]);
        }
        return yPredict;
    }

    public void outputResults(File fileInput, PrintStream output, double[][] Testx, int[] Testy, int max) throws Exception {
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

        // element of class in org.nerd.kid.arff file
        String[] dataClass = accessArff.readClassArff(fileInput);

        // calling the method of confusion matrix
        int[][] confusMatrix = evaluation.confusionMatrix(Testy, yPredict, max);
        int[] TP = evaluation.countingTruePositive(confusMatrix, max);
        int[] TN = evaluation.countingTrueNegative(confusMatrix, max);
        int[] FP = evaluation.countingFalsePositive(confusMatrix, max);
        int[] FN = evaluation.countingFalseNegative(confusMatrix, max);
        int totalAll = evaluation.countingTotalClass(confusMatrix, max);
        double[] resultPrecision = evaluation.Precision(TP, FP);
        double[] resultRecall = evaluation.Recall(TP, FN);
        double[] resultSpecificity = evaluation.Specificity(TN, FP);
        double[] resultFmeasure = evaluation.Fmeasure(resultPrecision, resultRecall);

        // classfied instances
        output.println("** Classification with Random Forest of " + forest.size() + " trees **");
        output.print("\n");
        output.format("Total of instances\t\t\t:\t %d \n", sizeDataAll);
        output.format("Number of instance trained\t\t:\t %d \n", sizeDataTrained);
        output.format("Number of instance predicted\t\t:\t %d \n", sizeDataPredicted);
        output.format("Correctly classified instances\t\t:\t %d (%.3f %%) %n", count_classified, count_classified / total_instances * 100.00);
        output.format("Incorrectly classified instances\t:\t %d (%.3f %%) %n", count_error, count_error / total_instances * 100.00);
        output.format("Out of Bag (OOB) error rate\t\t:\t %.3f%n", forest.error());
        output.format("Specificity\t\t\t\t:\t %.3f %n", evaluation.averageSpecificity(resultSpecificity));
        output.format("Accuracy\t\t\t\t:\t %.3f%n", evaluation.Accuracy(TP, totalAll));
        // FMeasure, Precision, Recall for all classes
        output.format("Macro Average Precision\t\t\t:\t %.3f%n", evaluation.averagePrecisionMacro(resultPrecision));
        output.format("Micro Average Precision\t\t\t:\t %.3f%n", evaluation.averagePrecisionMicro(TP, FP));
        output.format("Macro Average Recall\t\t\t:\t %.3f%n", evaluation.averageRecallMacro(resultRecall));
        output.format("Micro Average Recall\t\t\t:\t %.3f%n", evaluation.averageRecallMicro(TP, FN));
        output.format("Macro Average FMeasure\t\t\t:\t %.3f%n", evaluation.averageFmeasure(evaluation.averagePrecisionMacro(resultPrecision), evaluation.averageRecallMacro(resultRecall)));
        output.format("Micro Average FMeasure\t\t\t:\t %.3f%n", evaluation.averageFmeasure(evaluation.averagePrecisionMicro(TP, FP), evaluation.averageRecallMicro(TP, FN)));

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

        output.print("Class:\t\t");
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

        // FMeasure, Precision, Recall, Accuracy for every class
        output.println("\n** Validation for each class **");
        output.printf("\nClass\t\t:");
        for (int i = 0; i <= max; i++) {
            output.printf("\t" + i);
        }

        output.printf("\nFMeasure\t:");
        for (int i = 0; i <= max; i++) {
            output.format("\t%.2f", resultFmeasure[i]);
        }

        output.printf("\nPrecision\t:");
        for (int i = 0; i <= max; i++) {
            output.format("\t%.2f", resultPrecision[i]);
        }

        output.printf("\nRecall\t\t:");
        for (int i = 0; i <= max; i++) {
            output.format("\t%.2f", resultRecall[i]);
        }

        output.printf("\nSpesificity\t:");
        for (int i = 0; i <= max; i++) {
            output.format("\t%.2f", resultSpecificity[i]);
        }

        output.println("\n");
    }
}
