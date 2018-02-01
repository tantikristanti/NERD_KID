package org.nerd.kid.rest;

import org.nerd.kid.arff.ArffParser;
import smile.classification.RandomForest;
import smile.data.AttributeDataset;

import java.io.*;

/*
* class to predict test data
* */

public class DataPredictor {
    //classification model of Random Forest
    private RandomForest forest = null;
    private smile.data.parser.ArffParser arffParser = new smile.data.parser.ArffParser();
    private AttributeDataset attributeDataset = null;
    ArffParser accessArff = new ArffParser();

    public String[] predictNewTestData(double[][] testX) throws Exception{
        String file = "data/arff/Training.arff";
        // parsing the initial file to get the response index
        attributeDataset = arffParser.parse(new FileInputStream(file));

        // getting the response index
        int responseIndex = attributeDataset.attributes().length - 1;

        // setResponseIndex is response variable; for classification, it is the class label; for regression, it is of real value
        arffParser = new smile.data.parser.ArffParser().setResponseIndex(responseIndex);

        // parsing the file to get the dataset
        attributeDataset = arffParser.parse(file);

        double[][] datax = attributeDataset.toArray(new double[attributeDataset.size()][]);
        int[] datay = attributeDataset.toArray(new int[attributeDataset.size()]);

        // training with Random Forest classification
        forest = new RandomForest(attributeDataset.attributes(), datax, datay, 100);
        int[] resultPrediction = new int[testX.length];

        // getting the index label of the class
        String[] nameClass = accessArff.readClassArff(new File(file));
        for (int i = 0; i < testX.length; i++) {
            resultPrediction[i] = forest.predict(testX[i]);
        }
        String[] result = new String[testX.length];
        for (int i = 0; i < resultPrediction.length; i++) {
            int idx = resultPrediction[i];
            result[i] = nameClass[idx];
        }

        return result;
    }

}

