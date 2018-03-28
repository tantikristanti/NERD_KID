package org.nerd.kid.preannotation;

import org.nerd.kid.model.WikidataNERPredictor;

/*
main class for annotating Wikidata Ids from a testing input file
ex.

Input Testing File (to be predicted) : data/preannotation/dataPreannotation.csv
* */

public class MainPreAnnotation {
    public static void main(String[] args) throws Exception{
        WikidataNERPredictor wikidataNERPredictor = new WikidataNERPredictor();
        System.out.println("Processing the pre-annotation ...");
        wikidataNERPredictor.predictForPreannotation();
        System.out.println("Result in 'result/csv/ResultPredictedClass.csv'");
    }
}
