package org.nerd.kid.extractor;

import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws Exception{
        FeatureWikidataExtractor featureWikidataExtractor = new FeatureWikidataExtractor();
        Map<String, List<String>> result = featureWikidataExtractor.loadFeatures();
        for (Map.Entry<String, List<String>> entry : result.entrySet()) {
            System.out.println(entry.getKey()+": ");
            List<String> values = entry.getValue();
            for (String item : values){
                System.out.println("\t"+item);
            }
        }
    }
}
