package smile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.data.AttributeDataset;
import smile.data.parser.ArffParser;
import smile.classification.RandomForest;
import smile.math.Math;
import smile.validation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/* this class contains the use of Smile Java API for predictive modeling */
public class smileUsage {

    // --------------- attributes ---------------
    private ArffParser arffParser = null;
    private AttributeDataset attributeDataset = null;
    private AttributeDataset training = null;
    private AttributeDataset testing = null;
    //classification model
    private RandomForest forest = null;

    //logger
    private static final Logger logger =  LoggerFactory.getLogger(smileUsage.class);


    /* --------------- methods ---------------

     loading only training data */
    public void loadData(File file) throws Exception {
        // setResponseIndex is response variable; for classification, it is the class label; for regression, it is of real value
        arffParser = new ArffParser();
        arffParser.setResponseIndex(4);
        // dataset of a number of attributes
        attributeDataset = arffParser.parse(new FileInputStream(file));

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

    // training the model with Random Forest
    public void trainModel() throws Exception {
        if (attributeDataset == null){
            logger.debug("Training data doesn't exist");
        }

        logger.info("Training the model.");

        double[][] x = attributeDataset.toArray(new double[attributeDataset.size()][]);
        int[] y = attributeDataset.toArray(new int[attributeDataset.size()]);

        // maximum number of trees: 200
        forest = new RandomForest(x,y,200);
        System.out.println(forest.size());
    }

    /* validation model using LOOCV (leave-one-out cross validation);
        if it has only a single dataset for building models
     */
    public void validationModelLOOCV() throws Exception {

        // for getting output stream of the file for writing the result
        File fl = new File("result/result.txt");


        BufferedWriter result = new BufferedWriter(new FileWriter(fl));

        double[][] x = attributeDataset.toArray(new double[attributeDataset.size()][]);
        int[] y = attributeDataset.toArray(new int[attributeDataset.size()]);

        int leng = x.length;
        LOOCV loocv = new LOOCV(leng);

        int count_error = 0, count_classified =0;

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
        double total_instances = count_error+count_classified;
        result.write("Number of trees: " + forest.size());
        result.newLine();
        result.write("Correctly classified instances: " + count_classified + "(" + count_classified/total_instances*100.00 + " %)");
        result.newLine();
        result.write("Incorrectly classified instances: " + count_error + "(" + count_error/total_instances*100.00 + " %)");
        System.out.println("Number of trees: " + forest.size());
        System.out.format("Correctly classified instances: %d (%.3f %%) \n", count_classified, count_classified/total_instances*100.00);
        System.out.format("Incorrectly classified instances: %d (%.3f %%) \n", count_error, count_error/total_instances*100.00);
        result.close();
    }

    // validation model using training and testing data
    public void validationModel() throws Exception {
        double[][] trainx = training.toArray(new double[training.size()][]);
        int[] trainy = training.toArray(new int[training.size()]);
        double[][] testx = testing.toArray(new double[testing.size()][]);
        int[] testy = testing.toArray(new int[testing.size()]);

        forest = new RandomForest(attributeDataset.attributes(), trainx, trainy, 200);

        int count_error = 0, count_classified =0;
        for (int i = 0; i < testx.length; i++) {
            if (forest.predict(testx[i]) != testy[i]) {
                count_error++;
            }
        }
        double total_instances = count_error+count_classified;
        System.out.format("Correctly classified instances: %d (%.3f %%) \n", count_classified, count_classified/total_instances*100.00);
        System.out.format("Incorrectly classified instances: %d (%.3f %%) \n", count_error, count_error/total_instances*100.00);
    }



    public void selectionFeature(){

    }

}
