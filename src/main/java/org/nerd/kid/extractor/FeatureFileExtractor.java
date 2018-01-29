package org.nerd.kid.extractor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureFileExtractor {
    public Map<String, List<String>> loadFeatures() {
        try {
            return loadFeatures(new FileInputStream("data/resource/feature_mapper.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public Map<String, List<String>> loadFeatures(InputStream inputStreamFeatureFile) {
        Map<String, List<String>> featureMap = new HashMap<>();
        Reader featureMapperIn = new InputStreamReader(inputStreamFeatureFile);
        Iterable<CSVRecord> recordsFeatures = null;
        try {
            recordsFeatures = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(featureMapperIn);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }

        for (CSVRecord recordFeature : recordsFeatures) {
            String property = recordFeature.get("Property");
            String value = recordFeature.get("Value");

            // in order to get unique of property-value combination
            if (featureMap.keySet().contains(property)) {
                featureMap.get(property).add(value); // if a property-value exists, get it
            } else {
                List<String> values = new ArrayList<>();
                values.add(value);
                featureMap.put(property, values); // if there aren't exist yet, add a new one
            }
        }
        return featureMap;
    }

    public List<String> loadFeaturesNoValue() {
        try {
            return loadFeaturesNoValue(new FileInputStream("data/resource/feature_mapper_no_value.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<String> loadFeaturesNoValue(InputStream inputStreamFeatureNoValueFile){
        List<String> featureMapNoValue = new ArrayList<>();
        Reader featureMapperIn = new InputStreamReader(inputStreamFeatureNoValueFile);
        Iterable<CSVRecord> recordsFeaturesNoValue = null;
        try{
            recordsFeaturesNoValue = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(featureMapperIn);

        } catch (IOException e){
            e.printStackTrace();
            return new ArrayList<>();
        }

        for (CSVRecord recordFeatureNoValue : recordsFeaturesNoValue){
            String property = recordFeatureNoValue.get("Property");

            if(recordFeatureNoValue != null){
                featureMapNoValue.add(property);
            }
        }

        return featureMapNoValue;
    }

    public void printWikidataFeatures(Map<String, List<String>> result) {
        for (Map.Entry<String, List<String>> entry : result.entrySet()) {
            System.out.println(entry.getKey() + ": ");
            List<String> values = entry.getValue();
            for (String item : values) {
                System.out.println("\t" + item);
            }
        }
    }
}
