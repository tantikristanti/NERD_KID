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


    /* --------------- methods ---------------

     loading only training data */
    public void loadData(File file, int index) throws Exception {
        // setResponseIndex is response variable; for classification, it is the class label; for regression, it is of real value
        arffParser = new ArffParser();
        arffParser.setResponseIndex(index);

        // dataset of a number of attributes
        attributeDataset = arffParser.parse(new FileInputStream(file));

        // information about the file
        System.out.println("Loaded training data: " + file.getPath());
    }


    // loading training and testing data
    public void loadDataTrainTest(File file1, File file2) throws Exception {

        // setResponseIndex is response variable; for classification, it is the class label; for regression, it is of real value
        arffParser = new ArffParser().setResponseIndex(4);
        // dataset of a number of attributes
        training = arffParser.parse(new FileInputStream(file1));
        testing = arffParser.parse(new FileInputStream(file2));

        System.out.println("Loaded training data : " + file1.getPath());
        System.out.println("Loaded testing data : " + file2.getPath());

    }

    // splitting the model into training and data set
    public void splitModel(File file, int split) throws Exception {

        // for getting output stream of the file for writing the result
        File fl = new File("result/Result_" + file.getName() + ".txt");

        BufferedWriter result = new BufferedWriter(new FileWriter(fl));

        // datax is for the examples, datay is for the class
        double[][] datax = attributeDataset.toArray(new double[attributeDataset.size()][]);
        int[] datay = attributeDataset.toArray(new int[attributeDataset.size()]);

        // size of examples
        int n = datax.length;

        // size of examples after split in certain percentage
        int m = n * split / 100;
        int[] index = Math.permutate(n);

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

        for (int i = 0; i < testx.length; i++) {
            yPredict[i] = forest.predict(testx[i]);
            if (testy[i] != forest.predict(testx[i]))
                count_error++;
            else
                count_classified++;
        }

        // classified instances
        double total_instances = count_error + count_classified;

        // header in text file
        result.write("Classification with Random Forest of " + forest.size()+ " trees");
        result.newLine();
        result.write("Test mode is " + split + "% train, remainder is test data.");
        result.newLine();

        // out of bag error
        System.out.format("Out of Bag (OOB) error rate : %.4f%n", forest.error());
        result.write("Out of Bag (OOB) error rate : "+forest.error());
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
        index = QuickSort.sort(importance);
        int importance_length = importance.length;
        // i-- > 0 means comparing i > 0 and decrement i--
        for (int i = importance_length; i-- > 0; ) {
            System.out.format("%s importance is %.4f%n", attributeDataset.attributes()[index[i]], importance[i]);
            result.write(attributeDataset.attributes()[index[i]] + " importance is : " + importance[i]);
            result.newLine();
        }

        // getting the result of confusion matrix, precision and recall
        System.out.println("Confusion matrix: " + new ConfusionMatrix(testy, yPredict).toString());
        System.out.println("Precision: " + new Precision().measure(testy, yPredict));
        System.out.println("Recall: " + new Recall().measure(testy, yPredict));
        result.write("Confusion matrix: " + new ConfusionMatrix(testy, yPredict).toString());
        result.newLine();
        result.write("Precision: " + new Precision().measure(testy, yPredict));
        result.newLine();
        result.write("Recall: " + new Recall().measure(testy, yPredict));

        result.close();

    }


    // training the model with Random Forest
    public void trainModel() throws Exception {

        // if there isn't any training data
        if (attributeDataset == null) {
            logger.debug("Training data doesn't exist");
        }

        // training the data
        logger.info("Training the model.");

        double[][] x = attributeDataset.toArray(new double[attributeDataset.size()][]);
        int[] y = attributeDataset.toArray(new int[attributeDataset.size()]);

        // maximum number of trees: 100
        forest = new RandomForest(attributeDataset.attributes(), x, y, 100);

        int[] yPredict = new int[y.length];

        for (int i = 0; i < x.length; i++) {
            yPredict[i] = forest.predict(x[i]);

        }

        System.out.println("Confusion matrix: " + new ConfusionMatrix(y, yPredict).toString());
        System.out.println("Precission: " + new Precision().measure(y, yPredict));
        System.out.println("Recall: " + new Recall().measure(y, yPredict));
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

    // validation model using training and testing data
    public void validationModel() throws Exception {
        double[][] trainx = training.toArray(new double[training.size()][]);
        int[] trainy = training.toArray(new int[training.size()]);
        double[][] testx = testing.toArray(new double[testing.size()][]);
        int[] testy = testing.toArray(new int[testing.size()]);

        forest = new RandomForest(attributeDataset.attributes(), trainx, trainy, 200);

        int count_error = 0, count_classified = 0;
        for (int i = 0; i < testx.length; i++) {
            if (forest.predict(testx[i]) != testy[i]) {
                count_error++;
            }
        }
        double total_instances = count_error + count_classified;
        System.out.format("Correctly classified instances: %d (%.3f %%) \n", count_classified, count_classified / total_instances * 100.00);
        System.out.format("Incorrectly classified instances: %d (%.3f %%) \n", count_error, count_error / total_instances * 100.00);
    }


    public void selectionFeature() {

    }

}
