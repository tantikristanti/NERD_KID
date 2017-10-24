package org.nerd.kid.preannotation;

import org.nerd.kid.extractor.WikidataIdExtractor;

public class MainWikidataIdExtractor {
    public static void main(String[] args) throws Exception {
        System.out.println("Collect the elements in CSV file (data/preannotation/dataPreannotation.csv)");
        WikidataIdExtractor wikidataIdExtractor = new WikidataIdExtractor();
        wikidataIdExtractor.addElementFromFileJson();
    }
}
