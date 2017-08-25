package smile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smile.data.AttributeDataset;
import smile.data.parser.ArffParser;
import smile.classification.RandomForest;
import smile.math.Math;
import smile.sort.QuickSort;
import smile.validation.*;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.List;


/* this class contains the use of Smile Java API for predictive modeling */
public class smileUsage {

    // --------------- attributes ---------------
    private ArffParser arffParser = null;
    private AttributeDataset attributeDataset = null;
    private AttributeDataset training = null;
    private AttributeDataset testing = null;

    //classification model of Random Forest
    private RandomForest forest = null;

    //logger
    private static final Logger logger = LoggerFactory.getLogger(smileUsage.class);

    // --------------- methods ---------------

    // loading the model, if there is only the training data
    public void loadData(File file, int index) throws Exception {

        // setResponseIndex is response variable; for classification, it is the class label; for regression, it is of real value
        arffParser = new ArffParser().setResponseIndex(index);

        // dataset of a number of attributes
        attributeDataset = arffParser.parse(new FileInputStream(file));

        // information about the file
        System.out.println("Loading training data " + file.getPath() + " is finished succesfully.");
    }


    // loading the model, if there are training and testing data
    public void loadDataTrainTest(File file1, File file2, int index) throws Exception {

        // setResponseIndex is response variable; for classification, it is the class label; for regression, it is of real value
        arffParser = new ArffParser();
        arffParser.setResponseIndex(index);

        // dataset of a number of attributes
        training = arffParser.parse(new FileInputStream(file1));
        testing = arffParser.parse(new FileInputStream(file2));

        System.out.println("Loading training data : " + file1.getPath() + " is finished succesfully.");
        System.out.println("Loading testing data : " + file2.getPath() + " is finished succesfully.");

    }

    // validation using training and testing data
    public void trainTestModel(File file, int split) throws Exception {

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
        File fl = new File("result/Result_" + file.getName() + ".txt");

        BufferedWriter result = new BufferedWriter(new FileWriter(fl));

        // datax is for the examples, datay is for the class
        double[][] trainx = training.toArray(new double[training.size()][]);
        int[] trainy = training.toArray(new int[training.size()]);
        double[][] testx = testing.toArray(new double[testing.size()][]);
        int[] testy = testing.toArray(new int[testing.size()]);

        // training with Random Forest classification
        forest = new RandomForest(attributeDataset.attributes(), trainx, trainy, 100);

        // prediction and calculating the classes classified
        int[] yPredict = new int[testy.length];
        int count_error = 0, count_classified = 0;
        double[] fmeasurePerson = null;

        // counting class classified or not
        for (int i = 0; i < testx.length; i++) {
            yPredict[i] = forest.predict(testx[i]);
            if (testy[i] != yPredict[i]) {
                count_error++;
            } else
                count_classified++;
        }

        // classified instances
        double total_instances = count_error + count_classified;

        // header in text file
        result.write("** Classification with Random Forest of " + forest.size() + " trees **");
        result.newLine();
        result.write("Test mode is " + split + "% train, remainder is test data.");
        result.newLine();

        // classfied instances
        System.out.println("** Classification with Random Forest of " + forest.size() + " trees **");
        System.out.println("\n");
        System.out.format("Correctly classified instances: \t %d (%.3f %%) %n", count_classified, count_classified / total_instances * 100.00);
        System.out.format("Incorrectly classified instances: \t %d (%.3f %%) %n", count_error, count_error / total_instances * 100.00);
        System.out.format("Out of Bag (OOB) error rate : \t\t %.3f%n", forest.error());

        // Fmeasure, precission, recall for all classes
        FMeasure fmeasure = new FMeasure();
        Precision precision = new Precision();
        Recall recall = new Recall();
        Accuracy accuracy = new Accuracy();

        Double result_fmeasure = Double.isNaN(fmeasure.measure(testy, yPredict)) ? 0.0 : fmeasure.measure(testy, yPredict),
                result_precision = Double.isNaN(precision.measure(testy, yPredict)) ? 0.0 : precision.measure(testy, yPredict),
                result_recall = Double.isNaN(recall.measure(testy, yPredict)) ? 0.0 : recall.measure(testy, yPredict),
                result_accuracy = Double.isNaN(accuracy.measure(testy, yPredict)) ? 0.0 : accuracy.measure(testy, yPredict);

        //System.out.println("Confusion matrix: " + new ConfusionMatrix(testy, yPredict).toString());
        System.out.println("** Validation for all classes **");
        System.out.format("\nFMeasure: \t%.3f%n", result_fmeasure);
        System.out.format("Precision: \t%.3f%n", result_precision);
        System.out.format("Recall: \t%.3f%n", result_recall);
        System.out.format("Accuracy: \t%.3f %n%n", result_accuracy);

        // writing the result into file text
        result.newLine();
        result.write("Correctly classified instances: \t" + String.format("%d", count_classified) + " (" + String.format("%.3f %%", count_classified / total_instances * 100.00) + ")");
        result.newLine();
        result.write("Incorrectly classified instances: \t" + String.format("%d", count_error) + " (" + String.format("%.3f %%", count_error / total_instances * 100.00) + ")");
        result.newLine();
        result.write("Out of Bag (OOB) error rate : \t\t" + String.format("%.3f", forest.error()));
        result.newLine();
        result.write("** Validation for all classes **");
        result.newLine();
        result.write("FMeasure: \t" + String.format("%.3f", result_fmeasure));
        result.newLine();
        result.write("Precision: \t" + String.format("%.3f", result_precision));
        result.newLine();
        result.write("Recall: \t" + String.format("%.3f", result_recall));
        result.newLine();
        result.write("Accuracy: \t" + String.format("%.3f", result_accuracy));
        result.newLine();

        // Fmeasure, precission, recall for each class
        // for grouping the data
        HashMap<Integer, List<Integer>> testYPerClass = new HashMap<Integer, List<Integer>>();

        // grouping data by its classes
        for (int j = 0; j < testy.length; j++) {
            List<Integer> listData = new ArrayList<Integer>();

            int dataSearch = testy[j];
            // putting the data into HashMap
            if (!testYPerClass.containsKey(dataSearch)) {
                for (int k = 0; k < testy.length; k++) {
                    if (testy[k] == dataSearch)
                        listData.add(yPredict[k]);
                }
                testYPerClass.put(dataSearch, listData);
            }
        }



        /**
        // training the data
        logger.info("Training the model.");

        // Fmeasure, precission, recall
        FMeasure fmeasure = new FMeasure();
        Precision precision = new Precision();
        Recall recall = new Recall();

        // for getting output stream of the file for writing the result
        File fl = new File("result/Result_" + file.getName() + ".txt");

        BufferedWriter result = new BufferedWriter(new FileWriter(fl));

        double[][] trainx = training.toArray(new double[training.size()][]);
        int[] trainy = training.toArray(new int[training.size()]);
        double[][] testx = testing.toArray(new double[testing.size()][]);
        int[] testy = testing.toArray(new int[testing.size()]);

        // training with Random Forest classification
        forest = new RandomForest(attributeDataset.attributes(), trainx, trainy, 100);

        // prediction and calculating the classes classified
        int[] yPredict = new int[testy.length];
        int count_error = 0, count_classified = 0;
        for (int i = 0; i < testx.length; i++) {
            yPredict[i] = forest.predict(testx[i]);
            if (testy[i] != yPredict[i])
                count_error++;
            else
                count_classified++;
        }

        // classified instances
        double total_instances = count_error + count_classified;

        // header in text file
        result.write("Classification with Random Forest of " + forest.size() + " trees");
        result.newLine();
        result.write("Test mode is " + split + "% train, remainder is test data.");
        result.newLine();

        // out of bag error, method of measuring the prediction error
        System.out.format("Out of Bag (OOB) error rate : %.4f%n", forest.error());
        result.write("Out of Bag (OOB) error rate : " + forest.error());
        result.newLine();

        // classfied instances
        System.out.format("Correctly classified instances: %d (%.3f %%) \n", count_classified, count_classified / total_instances * 100.00);
        System.out.format("Incorrectly classified instances: %d (%.3f %%) \n", count_error, count_error / total_instances * 100.00);
        result.write("Correctly classified instances: " + count_classified + " (" + count_classified / total_instances * 100.00 + " %)");
        result.newLine();
        result.write("Incorrectly classified instances: " + count_error + " (" + count_error / total_instances * 100.00 + " %)");
        result.newLine();

        // searching the importance of variables
        double[] importance = forest.importance();
        int[] idx = QuickSort.sort(importance);
        int importance_length = importance.length;
        // i-- > 0 means comparing i > 0 and decrement i--
        for (int i = importance_length; i-- > 0; ) {
            System.out.format("%s importance is %.4f%n", attributeDataset.attributes()[idx[i]], importance[i]);
            result.write(attributeDataset.attributes()[idx[i]] + " importance is : " + importance[i]);
            result.newLine();
        }

        // getting the result of confusion matrix, precision and recall

        Double result_fmeasure = Double.isNaN(fmeasure.measure(testy, yPredict)) ? 0.0 : fmeasure.measure(testy, yPredict),
                result_precision = Double.isNaN(precision.measure(testy, yPredict)) ? 0.0 : precision.measure(testy, yPredict),
                result_recall = Double.isNaN(recall.measure(testy, yPredict)) ? 0.0 : recall.measure(testy, yPredict);

        //System.out.println("Confusion matrix: " + new ConfusionMatrix(testy, yPredict).toString());
        System.out.println("FMeasure: " + result_fmeasure);
        System.out.println("Precision: " + result_precision);
        System.out.println("Recall: " + result_recall);
        result.write("Confusion matrix: " + new ConfusionMatrix(testy, yPredict).toString());
        result.newLine();
        result.write("FMeasure: " + result_fmeasure);
        result.newLine();
        result.write("Precision: " + result_precision);
        result.newLine();
        result.write("Recall: " + result_recall);

        result.close();**/
    }

    // splitting the model into training and data set
    public void splitModel(File file, int split) throws Exception {

        // if there isn't any training data
        if (attributeDataset == null) {
            logger.debug("Training data doesn't exist");
        }

        // training the data
        logger.info("Training the model.");

        // for getting output stream of the file for writing the result
        File fl = new File("result/Result_" + file.getName() + ".txt");

        BufferedWriter result = new BufferedWriter(new FileWriter(fl));

        // datax is for the examples, datay is for the class
        double[][] datax = attributeDataset.toArray(new double[attributeDataset.size()][]);
        int[] datay = attributeDataset.toArray(new int[attributeDataset.size()]);

        // finding the biggest index in datay
        int max = datay[0];
        for (int i = 1; i < datay.length; i++) {
            if (datay[i] > max) {
                max = datay[i];
            }
        }

        // size of examples
        int n = datax.length;

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

        // prediction and calculating the classes classified
        int[] yPredict = new int[testy.length];
        int count_error = 0, count_classified = 0;

        // counting class classified or not
        for (int i = 0; i < testx.length; i++) {
            yPredict[i] = forest.predict(testx[i]);
            if (testy[i] != yPredict[i]) {
                count_error++;
            } else
                count_classified++;
        }

        // classified instances
        double total_instances = count_error + count_classified;

        // header in text file
        result.write("** Classification with Random Forest of " + forest.size() + " trees **");
        result.newLine();
        result.write("Test mode is " + split + "% train, remainder is test data.");
        result.newLine();

        // classfied instances
        System.out.println("** Classification with Random Forest of " + forest.size() + " trees **");
        System.out.println("\n");
        System.out.format("Correctly classified instances: \t %d (%.3f %%) %n", count_classified, count_classified / total_instances * 100.00);
        System.out.format("Incorrectly classified instances: \t %d (%.3f %%) %n", count_error, count_error / total_instances * 100.00);
        System.out.format("Out of Bag (OOB) error rate : \t\t %.3f%n", forest.error());

        // writing the result into file text
        result.newLine();
        result.write("Correctly classified instances: \t" + String.format("%d", count_classified) + " (" + String.format("%.3f %%", count_classified / total_instances * 100.00) + ")");
        result.newLine();
        result.write("Incorrectly classified instances: \t" + String.format("%d", count_error) + " (" + String.format("%.3f %%", count_error / total_instances * 100.00) + ")");
        result.newLine();
        result.write("Out of Bag (OOB) error rate : \t\t" + String.format("%.3f", forest.error()));
        result.newLine();

        // calling the method of confusion matrix
        int[][] confusMatrix = confusionMatrix(testy, yPredict, max);
        System.out.println("** Confusion Matrix **");
        result.write("** Confusion Matrix **\n");
        for (int i = 0; i <= max; i++) {
            for (int j = 0; j <= max; j++) {
                System.out.print(confusMatrix[i][j]+"\t");
                result.write(confusMatrix[i][j] + "\t");
            }
            System.out.print("\n");
            result.newLine();
        }

        // Fmeasure, precission, recall for all classes
        FMeasure fmeasure = new FMeasure();
        Precision precision = new Precision();
        Recall recall = new Recall();
        Accuracy accuracy = new Accuracy();

        Double result_fmeasure = Double.isNaN(fmeasure.measure(testy, yPredict)) ? 0.0 : fmeasure.measure(testy, yPredict),
                result_precision = Double.isNaN(precision.measure(testy, yPredict)) ? 0.0 : precision.measure(testy, yPredict),
                result_recall = Double.isNaN(recall.measure(testy, yPredict)) ? 0.0 : recall.measure(testy, yPredict),
                result_accuracy = Double.isNaN(accuracy.measure(testy, yPredict)) ? 0.0 : accuracy.measure(testy, yPredict);

        //System.out.println("Confusion matrix: " + new ConfusionMatrix(testy, yPredict).toString());
        System.out.println("** Validation for all classes **");
        System.out.format("\nFMeasure: \t%.3f%n", result_fmeasure);
        System.out.format("Precision: \t%.3f%n", result_precision);
        System.out.format("Recall: \t%.3f%n", result_recall);
        System.out.format("Accuracy: \t%.3f %n%n", result_accuracy);

        result.write("** Validation for all classes **");
        result.newLine();
        result.write("FMeasure: \t" + String.format("%.3f", result_fmeasure));
        result.newLine();
        result.write("Precision: \t" + String.format("%.3f", result_precision));
        result.newLine();
        result.write("Recall: \t" + String.format("%.3f", result_recall));
        result.newLine();
        result.write("Accuracy: \t" + String.format("%.3f", result_accuracy));
        result.newLine();

        // Fmeasure, precission, recall for each class
        // for grouping the data
        HashMap<Integer, List<Integer>> testYPerClass = new HashMap<Integer, List<Integer>>();

        // grouping data by its classes
        for (int j = 0; j < testy.length; j++) {
            List<Integer> listData = new ArrayList<Integer>();

            int dataSearch = testy[j];
            // putting the data into HashMap
            if (!testYPerClass.containsKey(dataSearch)) {
                for (int k = 0; k < testy.length; k++) {
                    if (testy[k] == dataSearch)
                        listData.add(yPredict[k]);
                }
                testYPerClass.put(dataSearch, listData);
            }
        }

        // accessing HashMap
        List<Double> FMeasureClass = new ArrayList<Double>();
        List<Double> PrecisionClass = new ArrayList<Double>();
        List<Double> RecallClass = new ArrayList<Double>();

        for (int i = 0; i <= max; i++) {
            int size = 0;
            if (testYPerClass.get(i) == null) {
                size = 0;
                FMeasureClass.add(0.0);
                PrecisionClass.add(0.0);
                RecallClass.add(0.0);
            } else {
                size = testYPerClass.get(i).size();
                int[] testYClass = new int[size];
                for (int j = 0; j < size; j++) {
                    testYClass[j] = i;
                }

                // converting List into Array
                int[] testYPredictClass = testYPerClass.get(i).stream().mapToInt(Integer::intValue).toArray();

                // testing
                System.out.println("TestYClass : ");
                for (int a = 0; a < testYClass.length; a++) {
                    System.out.print(testYClass[a] + ",");
                }
                System.out.println("\nTestYPredictClass : ");
                for (int a = 0; a < testYPredictClass.length; a++) {
                    System.out.print(testYPredictClass[a] + ",");

                    // this code is added in order to delete the bugs in smile
                    if (testYClass[a] != testYPredictClass[a]) {
                        testYClass[a] = 1;
                        testYPredictClass[a] = 0;
                    } else {
                        testYClass[a] = 1;
                        testYPredictClass[a] = 1;
                    }
                }

                System.out.println("\nF Measure :" + fmeasure.measure(testYClass, testYPredictClass));
                System.out.println("Precision :" + precision.measure(testYClass, testYPredictClass));
                System.out.println("Recall :" + recall.measure(testYClass, testYPredictClass));
                // end of testing

                // filling the List with the result of validation
                FMeasureClass.add(Double.isNaN(fmeasure.measure(testYClass, testYPredictClass)) ? 0.0 : fmeasure.measure(testYClass, testYPredictClass));
                PrecisionClass.add(Double.isNaN(precision.measure(testYClass, testYPredictClass)) ? 0.0 : precision.measure(testYClass, testYPredictClass));
                RecallClass.add(Double.isNaN(recall.measure(testYClass, testYPredictClass)) ? 0.0 : recall.measure(testYClass, testYPredictClass));
            }
        }

        // display result of validation per class
        System.out.println("** Validation for each class **");
        result.write("** Validation for each class **");
        result.newLine();

        System.out.printf("\nClass: \t");
        result.write("Class:\t");
        for (int i = 0; i <= max; i++) {
            System.out.printf("\t\t" + i);
            result.write("\t\t" + i);
        }

        System.out.printf("\nFMeasure\t");
        result.newLine();
        result.write("FMeasure: \t");
        for (int i = 0; i <= max; i++) {
            System.out.format("\t%.2f", FMeasureClass.get(i));
            result.write("\t" + String.format("%.2f", FMeasureClass.get(i)));
        }

        result.newLine();
        result.write("Precision: \t");
        System.out.printf("\nPrecision\t");
        for (int i = 0; i <= max; i++) {
            System.out.format("\t%.2f", PrecisionClass.get(i));
            result.write("\t" + String.format("%.2f", PrecisionClass.get(i)));
        }

        System.out.printf("\nRecall  \t");
        result.newLine();
        result.write("Recall: \t");
        for (int i = 0; i <= max; i++) {
            System.out.format("\t%.2f", RecallClass.get(i));
            result.write("\t" + String.format("%.2f", RecallClass.get(i)));
        }

        System.out.println("\n");
        result.newLine();

        // searching the importance of variables
        double[] importance = forest.importance();
        int[] idx = QuickSort.sort(importance);
        int importance_length = importance.length;
        System.out.println("** The importance for each property **");
        // i-- > 0 means comparing i > 0 and decrement i--
        for (int i = importance_length; i-- > 0; ) {
            System.out.format("%s importance is %.4f%n", attributeDataset.attributes()[idx[i]], importance[i]);
            result.write(attributeDataset.attributes()[idx[i]] + " importance is : " + importance[i]);
            result.newLine();
        }

        result.close();
    }


    /* validation model using LOOCV (leave-one-out cross validation);
        if it has only a single dataset for building models
     */
    public void validationModelLOOCV(File file) throws Exception {

        // for getting output stream of the file for writing the result
        File fl = new File("result/Result_" + file.getName() + ".txt");

        BufferedWriter result = new BufferedWriter(new FileWriter(fl));

        double[][] x = attributeDataset.toArray(new double[attributeDataset.size()][]);
        int[] y = attributeDataset.toArray(new int[attributeDataset.size()]);

        int leng = x.length;
        LOOCV loocv = new LOOCV(leng);

        int count_error = 0, count_classified = 0;

        for (int i = 0; i < leng; i++) {
            double[][] trainx = Math.slice(x, loocv.train[i]);
            int[] trainy = Math.slice(y, loocv.train[i]);

            forest = new RandomForest(attributeDataset.attributes(), trainx, trainy, 100);
            result.write(String.valueOf(forest));
            result.newLine();
            if (y[loocv.test[i]] != forest.predict(x[loocv.test[i]]))
                count_error++;
            else
                count_classified++;
        }
        double total_instances = count_error + count_classified;
        result.write("Number of trees: " + forest.size());
        result.newLine();
        result.write("Correctly classified instances: " + count_classified + " (" + count_classified / total_instances * 100.00 + " %)");
        result.newLine();
        result.write("Incorrectly classified instances: " + count_error + " (" + count_error / total_instances * 100.00 + " %)");
        System.out.println("Number of trees: " + forest.size());
        System.out.format("Correctly classified instances: %d (%.3f %%) \n", count_classified, count_classified / total_instances * 100.00);
        System.out.format("Incorrectly classified instances: %d (%.3f %%) \n", count_error, count_error / total_instances * 100.00);
        result.close();
    }

    public int[][] confusionMatrix(int[] testYClass, int[] testYPredictClass, int Idx) throws Exception {
        int[][] matrix = new int[Idx+1][Idx+1];

        for (int k=0; k<testYClass.length;k++) {
            int datax = testYClass[k];
            int datay = testYPredictClass[k];
            matrix[datax][datay]++;
        }

        return matrix;
    }

    public void Fmeasure(){

    }

    public void Precision(){

    }

    public void Recall(){

    }
}
