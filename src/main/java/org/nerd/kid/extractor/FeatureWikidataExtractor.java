package org.nerd.kid.extractor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.nerd.kid.data.WikidataElement;
import org.nerd.kid.data.WikidataElementInfos;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
* extract features (properties and values) of WikidataId directly from Wikidata knowledge base
* */

public class FeatureWikidataExtractor {
    private WikidataFetcherWrapper wikidataFetcherWrapper = new WikidataFetcherWrapper();
    private FeatureFileExtractor featureFileExtractor = new FeatureFileExtractor();

    public WikidataFetcherWrapper getWikidataFetcherWrapper() {
        return wikidataFetcherWrapper;
    }

    public void setWikidataFetcherWrapper(WikidataFetcherWrapper wikidataFetcherWrapper) throws Exception {
        this.wikidataFetcherWrapper = wikidataFetcherWrapper;
    }

    public List<WikidataElementInfos> getFeatureWikidata() throws Exception {
        return getFeatureWikidata(new File("data/csv/BaseElements.csv"));
    }

    public List<WikidataElementInfos> getFeatureWikidata(File inputFile) throws Exception {
        List<WikidataElementInfos> featureMatrix = new ArrayList<>();

        // count the number of features based on 'data/resource/feature_mapper.csv'
        int nbOfFeatures = 0;
        Map<String, List<String>> featuresMap = featureFileExtractor.loadFeatures();
        for (String key : featuresMap.keySet()) {
            nbOfFeatures += featuresMap.get(key).size();
        }

        Reader reader = new FileReader("data/csv/BaseElements.csv");
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records) {
            String wikidataId = record.get("WikidataID");
            String realClass = record.get("Class");

            // get information of id, label, features from Wikidata
            WikidataElement wikidataElement = wikidataFetcherWrapper.getElement(wikidataId);

            // set information of id, label, predicted class, features, real class
            WikidataElementInfos wikidataElementInfos = new WikidataElementInfos();
            wikidataElementInfos.setWikidataId(wikidataId);
            wikidataElementInfos.setLabel(wikidataElement.getLabel());
            wikidataElementInfos.setRealClass(realClass);

            // properties and values got directly from Wikidata
            Map<String, List<String>> propertiesWiki = wikidataElement.getProperties();

            // get the list of properties-values based on the result of 'data/resource/feature_mapper.csv'
            List<String> propertyValueFeatureMapper = new ArrayList<>();
            for (Map.Entry<String, List<String>> propertyGot : featuresMap.entrySet()) {
                String property = propertyGot.getKey();
                List<String> values = propertyGot.getValue();
                for (String value : values) {
                    String propertyValue = property + "_" + value;
                    propertyValueFeatureMapper.add(propertyValue);
                }
            }

            // get the list of properties-values based on the result directly from Wikidata
            List<String> propertyValueWikidata = new ArrayList<>();
            for (Map.Entry<String, List<String>> propertyGot : propertiesWiki.entrySet()) {
                String property = propertyGot.getKey();
                List<String> values = propertyGot.getValue();
                for (String value : values) {
                    String propertyValue = property + "_" + value;
                    propertyValueWikidata.add(propertyValue);
                }
            }


            /* compare two list of properties-values got from feature mapper and directly from Wikidata
                create new array list for the result of the comparison
                put 1 if certain property-value combination exists in both of lists and 0 if it's not found
             */
            Integer[] featureVector = new Integer[nbOfFeatures];

            int idx = 0;
            for (String propertyValue : propertyValueFeatureMapper) {
                if (propertyValueWikidata.contains(propertyValue)) {
                    featureVector[idx] = 1;
                }
                if (!propertyValueWikidata.contains(propertyValue)) {
                    featureVector[idx] = 0;
                }
                idx++;
            }

            // set information of feature vector
            wikidataElementInfos.setFeatureVector(featureVector);

            // set null for predicted class
            wikidataElementInfos.setPredictedClass("Null");

            featureMatrix.add(wikidataElementInfos);

        } // end of looping to read file that contains Wikidata Id and class

        return featureMatrix;
    } // end of method getFeatureWikidata

} // end of class FeatureWikidataExtractor
