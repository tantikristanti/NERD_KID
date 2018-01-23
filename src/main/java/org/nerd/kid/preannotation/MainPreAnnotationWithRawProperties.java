package org.nerd.kid.preannotation;

import org.nerd.kid.model.WikidataNERPredictor;

public class MainPreAnnotationWithRawProperties {
    public static void main(String[] args) throws Exception {
        WikidataNERPredictor wikidataNERPredictor = new WikidataNERPredictor();
        System.out.println("Processing the pre-annotation ...");
        wikidataNERPredictor.predictedResultAndProperties();
        System.out.println("Result in 'result/csv/ResultPredictedClassProperties.csv'");
    }
}
