package org.nerd.kid.arff;

import org.nerd.kid.data.WikidataElementInfos;
import org.nerd.kid.extractor.FeatureWikidataExtractor;

import java.io.File;
import java.util.List;

public class AddDataTrainerGenerator {
    public static void main(String[] args) throws Exception {
        /*ArffFileGenerator arffFileGenerator = new ArffFileGenerator();
        FeatureWikidataExtractor featureWikidataExtractor = new FeatureWikidataExtractor();

        if (arffFileGenerator.fileExist()) {
            arffFileGenerator.appendToFile();
            // add data
            List<WikidataElementInfos> resultFeatureWikidataExtractor = featureWikidataExtractor.getFeatureWikidata(new File("data/csv/NewElements.csv"));
            arffFileGenerator.addData(resultFeatureWikidataExtractor);
        }
        arffFileGenerator.close();
        System.out.print("Result can be seen in 'result/arff/Training.arff' ");*/
    }
}
