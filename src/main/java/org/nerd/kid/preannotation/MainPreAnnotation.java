package org.nerd.kid.preannotation;

import java.io.File;

public class MainPreAnnotation {
    public static void main(String[] args) throws Exception{
        System.out.println("Collect the elements in CSV file (data/preannotation/dataPreannotation.csv)");
        WikidataIdExtractor getDataElement = new WikidataIdExtractor();
        FeatureWikidataExtractor featureWikidataExtractor = new FeatureWikidataExtractor();
        //getDataElement.addElementFromFileJson();
        //getDataElement.getWikidataIdFromFileCsv();
        featureWikidataExtractor.getFeatureWikidata(new File("data/Training.arff"), new File("data/preannotation/dataPreannotation.csv"));
    }
}
