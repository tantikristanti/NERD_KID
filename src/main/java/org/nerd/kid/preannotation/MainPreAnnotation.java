package org.nerd.kid.preannotation;

import org.nerd.kid.model.WikidataNERPredictor;

/*
main class for annotating Wikidata Ids from a testing input file
ex.

Input Testing File (to be predicted) : data/preannotation/dataPreannotation.csv
* */

public class MainPreAnnotation {
    public static void main(String[] args) throws Exception{
//        String trainFile = "data/arff/Training.arff";
//        String newDataFile = "data/preannotation/dataPreannotation.csv";
//        String outputFile = "Predicted_Result.csv";
//        FeatureWikidataExtractor featureWikidataExtractor = new FeatureWikidataExtractor();
//        String[][] matrixNewData = featureWikidataExtractor.getFeatureWikidata(new File(trainFile), new File(newDataFile));
//
//        //print the result into file
//        featureWikidataExtractor.printResultWikidataExtractionWithoutProperties(matrixNewData, outputFile);

        WikidataNERPredictor wikidataNERPredictor = new WikidataNERPredictor();
        System.out.println("Processing the pre-annotation ...");
        wikidataNERPredictor.predict();
        System.out.println("Result in 'result/csv/ResultPredictedClass.csv'");
    }
}
