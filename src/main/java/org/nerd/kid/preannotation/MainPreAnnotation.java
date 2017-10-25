package org.nerd.kid.preannotation;

import org.nerd.kid.extractor.FeatureWikidataExtractor;

import java.io.File;
import java.util.Scanner;

/*
main class for annotating Wikidata Ids from a testing input file
ex.

Input Testing File (to be predicted) : data/preannotation/dataPreannotation.csv
* */

public class MainPreAnnotation {
    public static void main(String[] args) throws Exception{
        Scanner input = new Scanner(System.in);

        System.out.print("Input Training File ('[path]/[name of file].arff'; ex. 'data/arff/Training.arff') : ");
        String trainFile= input.nextLine();

        System.out.print("Input Testing File (to be predicted ex. 'data/preannotation/dataPreannotation.csv') : ");
        String testFile= input.nextLine();

        System.out.print("Name of output file : ");
        String outputFile = input.nextLine();

        System.out.println("Processing the pre-annotation ...");

        FeatureWikidataExtractor featureWikidataExtractor = new FeatureWikidataExtractor();
        String[][] matrixNewData = featureWikidataExtractor.getFeatureWikidata(new File(trainFile), new File(testFile));
        // print the result into file
        featureWikidataExtractor.printResultWikidataExtractionWithoutProperties(matrixNewData, outputFile);

        System.out.println("Result in 'result/csv/"+outputFile+".csv'");
    }
}
