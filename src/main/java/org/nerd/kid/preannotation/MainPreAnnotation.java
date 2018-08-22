package org.nerd.kid.preannotation;

import org.nerd.kid.model.WikidataNERPredictor;
import org.nerd.kid.service.NerdKidPaths;

import java.io.File;

/*
main class for annotating Wikidata Ids from a testing input file
ex.
* */

public class MainPreAnnotation {
    public static void main(String[] args) throws Exception{
        String fileInput = NerdKidPaths.DATA_CSV + "/NewElements.csv";
        String fileOutput = NerdKidPaths.RESULT_CSV + "/ResultPredictedClass.csv";

        WikidataNERPredictor wikidataNERPredictor = new WikidataNERPredictor();
        System.out.println("Processing the pre-annotation ...");
        wikidataNERPredictor.extractModel();
        wikidataNERPredictor.predictForPreannotation(new File(fileInput), new File(fileOutput));
    }
}
