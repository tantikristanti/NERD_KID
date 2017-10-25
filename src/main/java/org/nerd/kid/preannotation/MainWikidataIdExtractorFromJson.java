package org.nerd.kid.preannotation;

import org.nerd.kid.extractor.WikidataIdExtractor;

import java.util.Scanner;

/*
main class for collecting Wikidata Ids from one input file in format JSON
ex.

Name of output file : dataPreannotation

* */

public class MainWikidataIdExtractorFromJson {
    public static void main(String[] args) throws Exception {
        System.out.println("Collect the elements from JSON file ...");

        Scanner input = new Scanner(System.in);

        System.out.print("Name of output file : ");
        String outputFile= input.nextLine();

        WikidataIdExtractor wikidataIdExtractor = new WikidataIdExtractor();
        wikidataIdExtractor.addElementFromFileJson(outputFile);

        System.out.println("Result in 'data/preannotation/" +outputFile+".csv'" );
    }
}
