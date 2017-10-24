package org.nerd.kid.preannotation;

import org.nerd.kid.extractor.FeatureWikidataExtractor;

import java.io.File;
import java.util.Scanner;

/*
model class for annotating Wikidata Ids from a testing input file
ex.

Input Testing File (to be predicted) : data/preannotation/dataPreannotationCombination.csv
* */

public class MainPreAnnotation {
    public static void main(String[] args) throws Exception{
        Scanner input = new Scanner(System.in);

        System.out.print("Input Testing File (to be predicted) : ");
        String inputFile= input.nextLine();

        System.out.println("Processing the pre-annotation ...");

        FeatureWikidataExtractor featureWikidataExtractor = new FeatureWikidataExtractor();
        String[][] matrixNewData = featureWikidataExtractor.getFeatureWikidata(new File("data/Training.arff"), new File(inputFile));
        // print the result into file
        featureWikidataExtractor.printResultWikidataExtractionWithoutProperties(matrixNewData);

        System.out.println("Result in 'result/Predicted_Result.csv' ...");
    }
}
