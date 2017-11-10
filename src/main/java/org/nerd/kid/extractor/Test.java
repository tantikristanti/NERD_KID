package org.nerd.kid.extractor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.nerd.kid.data.WikidataElement;
import org.nerd.kid.data.WikidataElementInfos;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) throws Exception {

//        WikidataFetcherWrapper wikidataFetcherWrapper = new WikidataFetcherWrapper();
//        FeatureWikidataExtractor featureWikidataExtractor = new FeatureWikidataExtractor();
//        FeatureFileExtractor featureFileExtractor = new FeatureFileExtractor();
//
//        // OK
//
//        List<WikidataElementInfos> featureMatrix = new ArrayList<>();
//
//        // count the number of features based on 'data/resource/feature_mapper.csv'
//        int nbOfFeatures = 0;
//
//        // properties and values got from 'data/resource/feature_mapper.csv'
//        Map<String, List<String>> featuresMap = featureFileExtractor.loadFeatures();
//        for (String key : featuresMap.keySet()) {
//            nbOfFeatures += featuresMap.get(key).size();
//        }
//
//        Reader reader = new FileReader("data/csv/BaseElements.csv");
//        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
//        for (CSVRecord record : records) {
//            String wikidataId = record.get("WikidataID");
//            String realClass = record.get("Class");
//            System.out.println(wikidataId);
//
//            // get information of id, label, features from Wikidata
//            WikidataElement wikidataElement = wikidataFetcherWrapper.getElement(wikidataId);
//
//            // set information of id, label, predicted class, features, real class
//            WikidataElementInfos wikidataElementInfos = new WikidataElementInfos();
//            wikidataElementInfos.setWikidataId(wikidataId);
//            wikidataElementInfos.setLabel(wikidataElement.getLabel());
//            wikidataElementInfos.setRealClass(realClass);
//
//            Integer[] featureVector = new Integer[nbOfFeatures];
//
//            int idx = 0;
//
//            // properties and values got directly from Wikidata
//            Map<String, List<String>> propertiesWiki = wikidataElement.getProperties();
//
//            // just to print it out
//            System.out.println("WikidataElementInfos : " + wikidataElementInfos.getWikidataId() +
//                    "\t" + wikidataElementInfos.getLabel());
//
//            // get the list of properties-values based on the result of 'data/resource/feature_mapper.csv'
//            List<String> propertyValueFeatureMapper = new ArrayList<>();
//            for (Map.Entry<String, List<String>> propertyGot : featuresMap.entrySet()) {
//                String property = propertyGot.getKey();
//                List<String> values = propertyGot.getValue();
//                for (String value : values) {
//                    String propertyValue = property + "_" + value;
//                    propertyValueFeatureMapper.add(propertyValue);
//                }
//            }
//
//            // get the list of properties-values based on the result directly from Wikidata
//            List<String> propertyValueWikidata = new ArrayList<>();
//            for (Map.Entry<String, List<String>> propertyGot : propertiesWiki.entrySet()) {
//                String property = propertyGot.getKey();
//                List<String> values = propertyGot.getValue();
//                for (String value : values) {
//                    String propertyValue = property + "_" + value;
//                    propertyValueWikidata.add(propertyValue);
//                }
//            }
//
//            /* compare two list of properties-values got from feature mapper and directly from Wikidata
//                create new array list for the result of the comparison
//                put 1 if certain property-value combination exists in both of lists and 0 if it's not found
//             */
//
//            for (String propertyValue : propertyValueFeatureMapper) {
//                if (propertyValueWikidata.contains(propertyValue)) {
//                    featureVector[idx] = 1;
//                } if (!propertyValueWikidata.contains(propertyValue)) {
//                    featureVector[idx] = 0;
//                }
//                idx++;
//            }
//
//            // just to print it out
//            System.out.println("properties got from Feature Mapper");
//            for (String propertyValue : propertyValueFeatureMapper) {
//                System.out.print(propertyValue + "\t");
//            }
//
//            System.out.println("\n");
//
//            // just to print it out
//            System.out.println("properties got from Wikidata");
//            for (String propertyValue : propertyValueWikidata) {
//                System.out.print(propertyValue + "\t");
//            }
//
//            System.out.println("\n");
//
//            // just to print it out
//            System.out.println("Feature Vector");
//            for (Integer feature : featureVector) {
//                System.out.print(feature + "\t");
//            }
//
//            System.out.println("\n");
//
//            // set information of feature vector
//            wikidataElementInfos.setFeatureVector(featureVector);
//
//            // set null for predicted class
//            wikidataElementInfos.setPredictedClass("Null");
//
//            featureMatrix.add(wikidataElementInfos);
//
//            System.out.println("\n");


//        }


//        List<WikidataElementInfos> results = featureWikidataExtractor.getFeatureWikidata();
//        for (WikidataElementInfos result : results){
//            System.out.println(result.getWikidataId() + "\t" +result.getLabel());
//        }

        FeatureWikidataExtractor featureWikidataExtractor = new FeatureWikidataExtractor();
        List<WikidataElementInfos> resultFeatureWikidataExtractor = featureWikidataExtractor.getFeatureWikidata();
        for (WikidataElementInfos result : resultFeatureWikidataExtractor){
            System.out.println("\n");
            System.out.println(result.getWikidataId()+" - " +result.getLabel() + " - " +result.getRealClass()
                + "\t" + result.getPredictedClass());
            Integer[] features = result.getFeatureVector();
            for (Integer feature : features){
                System.out.print(feature + "\t");
            }
        }
    }
}
