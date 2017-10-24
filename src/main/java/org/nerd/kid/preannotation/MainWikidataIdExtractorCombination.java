package org.nerd.kid.preannotation;

import org.nerd.kid.extractor.WikidataIdExtractor;

import java.util.Scanner;

/*
model class for collecting Wikidata Ids from two input files different
ex.

Input File 1 : data/preannotation/dataPreannotation.csv
Input File 2 : data/corpus/csv/model0.wikipedia.csv

* */
public class MainWikidataIdExtractorCombination {
    public static void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);
        System.out.println("Getting Wikidata Id from several input files ...");

        System.out.print("Input File 1 : ");
        String input1= input.nextLine();

        System.out.print("Input File 2 : ");
        String input2= input.nextLine();

        WikidataIdExtractor wikidataIdExtractor = new WikidataIdExtractor();
        wikidataIdExtractor.getWikidataIdFromFilesCsv(input1, input2);

        System.out.println("The result of Wikidata Ids in data/preannotation/dataPreannotationCombination.csv");

    }
}
