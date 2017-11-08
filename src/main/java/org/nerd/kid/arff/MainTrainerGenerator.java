package org.nerd.kid.arff;

import org.nerd.kid.extractor.FeatureWikidataExtractor;

import java.util.List;
import java.util.Map;

/*
main class for generating Arff file

* */

public class MainTrainerGenerator {

    public static void main(String[] args) throws Exception {
        ArffFileGenerator arffFileGenerator = new ArffFileGenerator();
        FeatureWikidataExtractor featureWikidataExtractor = new FeatureWikidataExtractor();

        Map<String, List<String>> result = featureWikidataExtractor.loadFeatures();

        arffFileGenerator.createNewFile();
        arffFileGenerator.addHeader();

        for (Map.Entry<String, List<String>> entry : result.entrySet()) {
            List<String> values = entry.getValue();
            for (String item : values) {
                // entry.getKey():property; item:value
                String propertyValue = entry.getKey() + "_" + item;
                arffFileGenerator.addAttribute(propertyValue);
            }
        }



        arffFileGenerator.close();

        System.out.print("Result can be seen in 'result/arff/Training.arff' ");


    }
}
