package smile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smile.data.AttributeDataset;
import smile.data.parser.ArffParser;
import smile.classification.RandomForest;
import smile.math.Math;
import smile.sort.QuickSort;

import java.io.*;
import java.lang.*;

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

    // --------------- variables ---------------
    int max = 0;

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

        int maxTemp = trainy[0];
        // finding the biggest index in trainy
        for (int i = 1; i < trainy.length; i++) {
            if (trainy[i] >= maxTemp) {
                maxTemp = trainy[i];
            }
        }
        // finding the biggest index in testy
        for (int i = 1; i < testy.length; i++) {
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
        printScreenResult(testx, testy);

        // creating text file from the result
        writeToFileResult(file, testx, testy);
    }

    // splitting the model into training and data set
    public void splitModel(File file, int split) throws Exception {
        // if there isn't any training data
        if (attributeDataset == null) {
            logger.debug("Training data doesn't exist");
        }

        // training the data
        logger.info("Training the model.");

        // datax is for the examples, datay is for the class
        double[][] datax = attributeDataset.toArray(new double[attributeDataset.size()][]);
        int[] datay = attributeDataset.toArray(new int[attributeDataset.size()]);

        // finding the biggest index in datay
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

        // printing the result
        printScreenResult(testx, testy);

        // creating text file from the result
        writeToFileResult(file, testx, testy);
    }

    // confusion matrix
    public int[][] confusionMatrix(int[] testYClass, int[] testYPredictClass, int Idx) throws Exception {
        int[][] matrix = new int[Idx + 1][Idx + 1];

        for (int k = 0; k < testYClass.length; k++) {
            int datax = testYClass[k];
            int datay = testYPredictClass[k];
            matrix[datax][datay]++;
        }
        return matrix;
    }

    // printing the result of TP, TN, FP, FN
    public void printTrueFalsePositiveNegatif(int[][] matrix, int Idx) {
        int[] TP = calculTruePositive(matrix, Idx);
        int[] TN = calculTrueNegative(matrix, Idx);
        int[] FP = calculFalsePositive(matrix, Idx);
        int[] FN = calculFalseNegative(matrix, Idx);
        int[] totalEachClass = calculRowClass(matrix, Idx);
        int[] totalEachColumn = calculColumnClass(matrix, Idx);
        int totalAll = calculTotalClass(matrix, Idx);

        System.out.println("Total all data " + totalAll);
        System.out.println("\nTotal each class");
        for (int j = 0; j < totalEachClass.length; j++) {
            System.out.print(totalEachClass[j] + "\t");
        }

        System.out.println("\nTotal each column");
        for (int j = 0; j < totalEachColumn.length; j++) {
            System.out.print(totalEachColumn[j] + "\t");
        }

        System.out.println("\nTrue positives");
        for (int j = 0; j < TP.length; j++) {
            System.out.print(TP[j] + "\t");
        }

        System.out.println("\nFalse negatives");
        for (int j = 0; j < FN.length; j++) {
            System.out.print(FN[j] + "\t");
        }

        System.out.println("\nFalse positives");
        for (int j = 0; j < FP.length; j++) {
            System.out.print(FP[j] + "\t");
        }

        System.out.println("\nTrue negatives");
        for (int j = 0; j < TN.length; j++) {
            System.out.print(TN[j] + "\t");
        }
    }

    public int calculTotalClass(int[][] matrix, int Idx) {
        int totalAll = 0;

        // calculating total instance of all class
        for (int i = 0; i <= Idx; i++) {
            for (int j = 0; j <= Idx; j++) {
                totalAll += matrix[i][j];
            }
        }
        return totalAll;
    }

    public int[] calculRowClass(int[][] matrix, int Idx) {
        int[] totalEachClass = new int[Idx + 1];

        // calculating each row of class
        for (int i = 0; i <= Idx; i++) {
            totalEachClass[i] = 0;
            for (int j = 0; j <= Idx; j++) {
                totalEachClass[i] += matrix[i][j];
            }
        }
        return totalEachClass;
    }

    public int[] calculColumnClass(int[][] matrix, int Idx) {
        int[] totalEachColumn = new int[Idx + 1];

        // calculating total for each column
        for (int i = 0; i <= Idx; i++) {
            totalEachColumn[i] = 0;
            for (int j = 0; j <= Idx; j++) {
                totalEachColumn[i] += matrix[j][i];
            }
        }
        return totalEachColumn;
    }

    public int[] calculTruePositive(int[][] matrix, int Idx) {
        int[] TP = new int[Idx + 1];

        // calculating TP for each class
        for (int i = 0; i <= Idx; i++) {
            for (int j = 0; j <= Idx; j++) {
                if (i == j) {
                    TP[i] += matrix[i][j];
                }
            }
        }
        return TP;
    }

    public int[] calculFalseNegative(int[][] matrix, int Idx) {
        int[] TP = calculTruePositive(matrix, Idx);
        int[] FN = new int[Idx + 1];
        int[] totalEachClass = calculRowClass(matrix, Idx);

        // calculating FN for each class
        for (int i = 0; i <= Idx; i++) {
            FN[i] = totalEachClass[i] - TP[i];
        }
        return FN;
    }

    public int[] calculFalsePositive(int[][] matrix, int Idx) {
        int[] TP = calculTruePositive(matrix, Idx);
        int[] FP = new int[Idx + 1];
        int[] totalEachColumn = calculColumnClass(matrix, Idx);

        // calculating FP for each class
        for (int i = 0; i <= Idx; i++) {
            FP[i] = totalEachColumn[i] - TP[i];
        }
        return FP;
    }

    public int[] calculTrueNegative(int[][] matrix, int Idx) {
        int[] TP = calculTruePositive(matrix, Idx);
        int[] TN = new int[Idx + 1];
        int[] totalEachClass = calculRowClass(matrix, Idx);
        int[] totalEachColumn = calculColumnClass(matrix, Idx);
        int totalAll = calculTotalClass(matrix, Idx);

        // calculating TN for each class
        for (int i = 0; i <= Idx; i++) {
            TN[i] = totalAll - (totalEachClass[i] + totalEachColumn[i] - TP[i]);
        }

        return TN;
    }

    // accuracy of all class = TP of all class / number of all instances
    public double Accuracy(int[] TP, int calculTotal) {
        double totalTP = 0.0;
        for (int i = 0; i < TP.length; i++) {
            totalTP += TP[i];
        }
        return ((float) (totalTP / calculTotal));
    }

    /**
     Precision: Given all the predicted labels (for a given class X), how many instances were correctly predicted?
     Recall: For all instances that should have a label X, how many of these were correctly captured?
     **/

    // precision = TP / (TP + FP)
    public double[] Precision(int[] TP, int[] FP) {
        double[] resultPrecision = new double[TP.length];
        for (int i = 0; i < TP.length; i++) {
            resultPrecision[i] = Double.isNaN((float) TP[i] / (TP[i] + FP[i])) ? 0.0 : (float) TP[i] / (TP[i] + FP[i]);
        }
        return resultPrecision;
    }

    // Recall = sensitivity = TP / (TP + FN)
    public double[] Recall(int[] TP, int[] FN) {
        double[] resultRecall = new double[TP.length];
        for (int i = 0; i < TP.length; i++) {
            resultRecall[i] = Double.isNaN((float) TP[i] / (TP[i] + FN[i])) ? 0.0 : (float) TP[i] / (TP[i] + FN[i]);
        }
        return resultRecall;
    }

    // Specificity = true negative rate = TN / (TN + FP)
    public double[] Specificity(int[] TN, int[] FP) {
        double[] resultSpecificity = new double[TN.length];
        for (int i = 0; i < TN.length; i++) {
            resultSpecificity[i] = Double.isNaN((float) TN[i] / (TN[i] + FP[i])) ? 0.0 : (float) TN[i] / (TN[i] + FP[i]);
        }
        return resultSpecificity;
    }

    public double[] Fmeasure(double[] Precision, double[] Recall) {
        // Fmeasure = 2 * (precision * recall) / (precision + recall)
        double[] resultFmeasure = new double[Precision.length];
        for (int i = 0; i < Precision.length; i++) {
            resultFmeasure[i] = Double.isNaN((float) 2 * (Precision[i] * Recall[i]) / (Precision[i] + Recall[i])) ? 0.0 : (float) (float) 2 * (Precision[i] * Recall[i]) / (Precision[i] + Recall[i]);
        }
        return resultFmeasure;
    }

    // the average of FMeasure
    public double allFmeasure(double Precision, double Recall) {
        double resultAllFmeasure = Double.isNaN((float) 2 * (Precision * Recall) / (Precision + Recall)) ? 0.0 : (float) 2 * (Precision * Recall) / (Precision + Recall);
        return resultAllFmeasure;
    }

    // the average of Precision macro
    public double allPrecisionMacro(double[] Precision) {
        double resultAllPrecision = 0.0, subTotal = 0.0;
        for (int i = 0; i < Precision.length; i++) {
            subTotal += Precision[i];
        }
        resultAllPrecision = Double.isNaN((float) subTotal / Precision.length) ? 0.0 : (float) subTotal / Precision.length;
        return resultAllPrecision;
    }

    // the average of Precision micro
    public double allPrecisionMicro(int[] TP, int[] FP) {
        double resultAllPrecision = 0.0, subTotalTP = 0.0, subTotalFP = 0.0;
        for (int i = 0; i < TP.length; i++) {
            subTotalTP += TP[i];
            subTotalFP += FP[i];
        }
        resultAllPrecision = Double.isNaN((float) subTotalTP / (subTotalTP + subTotalFP)) ? 0.0 : (float) (float) subTotalTP / (subTotalTP + subTotalFP);
        return resultAllPrecision;
    }

    // the average of Recall macro
    public double allRecallMacro(double[] Recall) {
        double resultAllRecall = 0.0;
        double subTotal = 0.0;
        for (int i = 0; i < Recall.length; i++) {
            subTotal += Recall[i];
        }
        resultAllRecall = Double.isNaN((float) subTotal / Recall.length) ? 0.0 : (float) subTotal / Recall.length;
        return resultAllRecall;
    }

    // the average of Recall micro
    public double allRecallMicro(int[] TP, int[] FN) {
        double resultAllRecall = 0.0, subTotalTP = 0.0, subTotalFN = 0.0;
        for (int i = 0; i < TP.length; i++) {
            subTotalTP += TP[i];
            subTotalFN += FN[i];
        }
        resultAllRecall = Double.isNaN((float) subTotalTP / (subTotalTP + subTotalFN)) ? 0.0 : (float) (float) subTotalTP / (subTotalTP + subTotalFN);
        return resultAllRecall;
    }

    // the average of Specificity
    public double allSpecificity(double[] Specificity) {
        double resultAllSpecificity = 0.0;
        double subTotal = 0.0;
        for (int i = 0; i < Specificity.length; i++) {
            subTotal += Specificity[i];
        }
        resultAllSpecificity = Double.isNaN((float) subTotal / Specificity.length) ? 0.0 : (float) subTotal / Specificity.length;
        return resultAllSpecificity;
    }

    public int[] predictTestData(double[][] Testx){
        int[] yPredict = new int[Testx.length];
        // predicting the test
        for (int i = 0; i < Testx.length; i++) {
            yPredict[i] = forest.predict(Testx[i]);
        }
        return yPredict;
    }

    public int calculInstanceClassified(int[] Testy, int[] yPredict) {
        // calculating the classes classified
        int count_classified = 0;

        // counting class classified or not
        for (int i = 0; i < Testy.length; i++) {
            if (Testy[i] == yPredict[i]) {
                count_classified++;
            }
        }
        return count_classified;
    }

    public int calculInstanceNotClassified(int[] Testy, int[] yPredict) {
        // calculating the classes classified
        int count_error = 0;

        // counting class classified or not
        for (int i = 0; i < Testy.length; i++) {
            if (Testy[i] != yPredict[i]) {
                count_error++;
            }
        }
        return count_error;
    }

    public void printScreenResult(double[][] Testx, int[] Testy) throws Exception {
        // prediction and calculating the classes classified
        int[] yPredict = predictTestData(Testx);

        // counting class classified or not
        int count_error = calculInstanceNotClassified(Testy,  yPredict) ;
        int count_classified = calculInstanceClassified(Testy,  yPredict);

        // total instances
        double total_instances = count_error + count_classified;

        // calling the method of confusion matrix
        int[][] confusMatrix = confusionMatrix(Testy, yPredict, max);
        int[] TP = calculTruePositive(confusMatrix, max);
        int[] TN = calculTrueNegative(confusMatrix, max);
        int[] FP = calculFalsePositive(confusMatrix, max);
        int[] FN = calculFalseNegative(confusMatrix, max);
        int totalAll = calculTotalClass(confusMatrix, max);
        double[] resultPrecision = Precision(TP, FP);
        double[] resultRecall = Recall(TP, FN);
        double[] resultSpecificity = Specificity(TN, FP);
        double[] resultFmeasure = Fmeasure(resultPrecision, resultRecall);

        // classfied instances
        System.out.println("** Classification with Random Forest of " + forest.size() + " trees **");
        System.out.print("\n");
        System.out.format("Number of instance\t\t\t\t\t:\t %.0f \n", total_instances);
        System.out.format("Correctly classified instances\t\t:\t %d (%.3f %%) %n", count_classified, count_classified / total_instances * 100.00);
        System.out.format("Incorrectly classified instances\t:\t %d (%.3f %%) %n", count_error, count_error / total_instances * 100.00);
        System.out.format("Out of Bag (OOB) error rate\t\t\t:\t %.3f%n", forest.error());
        System.out.println("\n** Confusion Matrix **");
        System.out.println("Row: Actual; Column: Predicted");
        System.out.print("Class:\t");
        for (int i = 0; i <= max; i++) {
            System.out.print(i + "\t");
        }
        System.out.print("\n");
        for (int i = 0; i <= max; i++) {
            System.out.print("\t" + i + "|\t");
            for (int j = 0; j <= max; j++) {
                System.out.print(confusMatrix[i][j] + "\t");
            }
            System.out.print("\n");
        }

        // FMeasure, Precision, Recall, Accuracy for all classes
        System.out.println("\n** Validation for all classes **");
        System.out.format("Accuracy\t\t\t\t:\t%.3f%n", Accuracy(TP, totalAll));
        System.out.format("Macro Average Precision\t:\t%.3f%n", allPrecisionMacro(resultPrecision));
        System.out.format("Micro Average Precision\t:\t%.3f%n", allPrecisionMicro(TP, FP));
        System.out.format("Macro Average Recall\t:\t%.3f%n", allRecallMacro(resultRecall));
        System.out.format("Micro Average Recall\t:\t%.3f%n", allRecallMicro(TP, FN));
        System.out.format("Macro Average FMeasure\t:\t%.3f%n", allFmeasure(allPrecisionMacro(resultPrecision),allRecallMacro(resultRecall)));
        System.out.format("Micro AverageFMeasure\t:\t%.3f%n", allFmeasure(allPrecisionMicro(TP, FP),allRecallMicro(TP, FN)));
        System.out.format("Specificity\t\t\t\t:\t%.3f %n%n", allSpecificity(resultSpecificity));

        // FMeasure, Precision, Recall, Accuracy for every class
        System.out.println("** Validation for each class **");
        System.out.printf("\nClass\t\t:");
        for (int i = 0; i <= max; i++) {
            System.out.printf("\t\t" + i);
        }

        System.out.printf("\nFMeasure\t:\t");
        for (int i = 0; i <= max; i++) {
            System.out.format("\t%.2f", resultFmeasure[i]);
        }

        System.out.printf("\nPrecision\t:\t");
        for (int i = 0; i <= max; i++) {
            System.out.format("\t%.2f", resultPrecision[i]);
        }

        System.out.printf("\nRecall\t\t:\t");
        for (int i = 0; i <= max; i++) {
            System.out.format("\t%.2f", resultRecall[i]);
        }

        System.out.printf("\nSpesificity\t:\t");
        for (int i = 0; i <= max; i++) {
            System.out.format("\t%.2f", resultSpecificity[i]);
        }

        System.out.println("\n");

        // searching the importance of variables
        double[] importance = forest.importance();
        int[] idx = QuickSort.sort(importance);
        int importance_length = importance.length;
        System.out.println("** The importance for each property (%) **");
        // i-- > 0 means comparing i > 0 and decrement i--
        for (int i = importance_length; i-- > 0; ) {
            System.out.format("%s : %.4f%n", attributeDataset.attributes()[idx[i]], importance[i]);
        }
    }

    public void writeToFileResult(File file, double[][] Testx, int[] Testy) throws Exception {
        // prediction and calculating the classes classified
        int[] yPredict = predictTestData(Testx);

        // counting class classified or not
        int count_error = calculInstanceNotClassified(Testy,  yPredict) ;
        int count_classified = calculInstanceClassified(Testy,  yPredict);

        // total instances
        double total_instances = count_error + count_classified;

        // calling the method of confusion matrix
        int[][] confusMatrix = confusionMatrix(Testy, yPredict, max);
        int[] TP = calculTruePositive(confusMatrix, max);
        int[] TN = calculTrueNegative(confusMatrix, max);
        int[] FP = calculFalsePositive(confusMatrix, max);
        int[] FN = calculFalseNegative(confusMatrix, max);
        int totalAll = calculTotalClass(confusMatrix, max);
        double[] resultPrecision = Precision(TP, FP);
        double[] resultRecall = Recall(TP, FN);
        double[] resultSpecificity = Specificity(TN, FP);
        double[] resultFmeasure = Fmeasure(resultPrecision, resultRecall);

        // for getting output stream of the file for writing the result
        File fl = new File("result/Result_" + file.getName() + ".txt");

        BufferedWriter result = new BufferedWriter(new FileWriter(fl));

        // header in text file
        result.write("** Classification with Random Forest of " + forest.size() + " trees **");
        result.newLine();

        // writing the result into file text
        result.newLine();
        result.write("Number of instance\t\t\t\t\t:\t" + String.format("%.0f", total_instances));
        result.newLine();
        result.write("Correctly classified instances\t\t:\t" + String.format("%d", count_classified) + " (" + String.format("%.3f %%", count_classified / total_instances * 100.00) + ")");
        result.newLine();
        result.write("Incorrectly classified instances\t:\t" + String.format("%d", count_error) + " (" + String.format("%.3f %%", count_error / total_instances * 100.00) + ")");
        result.newLine();
        result.write("Out of Bag (OOB) error rate\t\t\t:\t" + String.format("%.3f", forest.error()));
        result.newLine();

        // calling the method of confusion matrix
        result.write("\n** Confusion Matrix **\n");
        result.write("Row: Actual; Column: Predicted \n");
        result.write("Class:\t");
        for (int i = 0; i <= max; i++) {
            result.write(i + "\t");
        }
        result.newLine();
        for (int i = 0; i <= max; i++) {
            result.write("\t" + i + "|\t");
            for (int j = 0; j <= max; j++) {
                result.write(confusMatrix[i][j] + "\t");
            }
            result.newLine();
        }
        result.write("\n** Validation for all classes **");
        result.newLine();
        result.write("Accuracy\t\t\t\t:\t" + String.format("%.3f", Accuracy(TP, totalAll)));
        result.newLine();
        result.write("Macro Average Precision\t:\t" + String.format("%.3f", allPrecisionMacro(resultPrecision)));
        result.newLine();
        result.write("Micro Average Precision\t:\t" + String.format("%.3f", allPrecisionMicro(TP, FP)));
        result.newLine();
        result.write("Macro Average Recall\t:\t" + String.format("%.3f", allRecallMacro(resultRecall)));
        result.newLine();
        result.write("Micro Average Recall\t:\t" + String.format("%.3f", allRecallMicro(TP, FN)));
        result.newLine();
        result.write("Macro Average FMeasure\t:\t" + String.format("%.3f", allFmeasure(allPrecisionMacro(resultPrecision),allRecallMacro(resultRecall))));
        result.newLine();
        result.write("Micro Average FMeasure\t:\t" + String.format("%.3f", allFmeasure(allPrecisionMicro(TP, FP),allRecallMicro(TP, FN))));
        result.newLine();
        result.write("Specificity\t\t\t\t:\t" + String.format("%.3f%n", allSpecificity(resultSpecificity)));
        result.newLine();

        // FMeasure, Precision, Recall, Accuracy for every class
        result.write("** Validation for each class **");
        result.newLine();
        result.write("Class\t\t:");
        for (int i = 0; i <= max; i++) {
            result.write("\t\t" + i);
        }
        result.newLine();
        result.write("FMeasure\t:\t");
        for (int i = 0; i <= max; i++) {
            result.write("\t" + String.format("%.2f", resultFmeasure[i]));
        }
        result.newLine();
        result.write("Precision\t:\t");
        for (int i = 0; i <= max; i++) {
            result.write("\t" + String.format("%.2f", resultPrecision[i]));
        }
        result.newLine();
        result.write("Recall\t\t:\t");
        for (int i = 0; i <= max; i++) {
            result.write("\t" + String.format("%.2f", resultRecall[i]));
        }
        result.newLine();
        result.write("Specificity\t:\t");
        for (int i = 0; i <= max; i++) {
            result.write("\t" + String.format("%.2f", resultSpecificity[i]));
        }
        result.newLine();

        // searching the importance of variables
        double[] importance = forest.importance();
        int[] idx = QuickSort.sort(importance);
        int importance_length = importance.length;
        result.write("\n** The importance for each property (%) **\n");
        // i-- > 0 means comparing i > 0 and decrement i--
        for (int i = importance_length; i-- > 0; ) {
            result.write(attributeDataset.attributes()[idx[i]] + " : " + importance[i]);
            result.newLine();
        }

        // ending the buffer
        result.close();
    }
}
