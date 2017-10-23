package org.nerd.kid.preannotation;

import java.io.File;

public class MainPreAnnotation {
    public static void main(String[] args) throws Exception{
        System.out.println("Processing the pre-annotation ...");
        FeatureWikidataExtractor featureWikidataExtractor = new FeatureWikidataExtractor();
        featureWikidataExtractor.getFeatureWikidata(new File("data/Training.arff"), new File("data/preannotation/dataPreannotation.csv"));
    }
}
