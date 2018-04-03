package org.nerd.kid.extractor;

import org.nerd.kid.data.WikidataElement;
import org.nerd.kid.data.WikidataElementInfos;
import org.nerd.kid.extractor.wikidata.WikidataFetcherWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * extract features (properties and values) of WikidataId directly from Wikidata knowledge base
 **/
public class FeatureWikidataExtractor {
    private WikidataFetcherWrapper wikidataFetcherWrapper = null;
    private FeatureFileExtractor featureFileExtractor = new FeatureFileExtractor();

    public WikidataFetcherWrapper getWikidataFetcherWrapper() {
        return wikidataFetcherWrapper;
    }

    public FeatureWikidataExtractor(WikidataFetcherWrapper wikidataFetcherWrapper){
        this.wikidataFetcherWrapper = wikidataFetcherWrapper;
    }

    public void setWikidataFetcherWrapper(WikidataFetcherWrapper wikidataFetcherWrapper) {
        this.wikidataFetcherWrapper = wikidataFetcherWrapper;
    }

    // method to get wikidataId, label, real-predicted class, and properties in binary form (0-1) based on the feature_mapper and Nerd KB
    public WikidataElementInfos getFeatureWikidata(String wikidataId) {
        /* count the number of features based on 'data/resource/feature_mapper.csv'
            and number of features based on the data/resource/feature_mapper_no_value.csv'
         */
        Map<String, List<String>> featuresMap = new HashMap<>();
        List<String> featuresNoValueList = new ArrayList<>();

        int nbOfFeatures = 0; // number of features of feature_mapper.csv
        try {
            featuresMap = featureFileExtractor.loadFeatures();
            for (String key : featuresMap.keySet()) {
                nbOfFeatures += featuresMap.get(key).size();
            }

            featuresNoValueList = featureFileExtractor.loadFeaturesNoValue();
            for (String feature : featuresNoValueList) {
                nbOfFeatures++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // get the element based on the wrapper whether from Wikidata or Nerd KB
        WikidataElement wikidataElement = new WikidataElement();
        try {
            wikidataElement = wikidataFetcherWrapper.getElement(wikidataId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // set information of id, label, predicted class, features, real class
        WikidataElementInfos wikidataElementInfos = new WikidataElementInfos();
        wikidataElementInfos.setWikidataId(wikidataId);
        wikidataElementInfos.setLabel(wikidataElement.getLabel());

        // properties and values got directly from Wikidata or Nerd KB (it depends on the implementation of the WikidataFetcherWrapper interface)
        Map<String, List<String>> propertiesWiki = wikidataElement.getProperties();

        // get the list of properties based on the result of 'data/resource/feature_mapper_no_value.csv'
        List<String> propertyNoValueFeatureMapper = new ArrayList<>();
        for (String propertyNoValueGot : featuresNoValueList) {
            propertyNoValueFeatureMapper.add(propertyNoValueGot);
        }

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
        // get the list of properties-values based on the result directly from Wikidata or Nerd KB
        List<String> propertyValueKB = new ArrayList<>();
        List<String> propertyNoValueKB = new ArrayList<>();
        for (Map.Entry<String, List<String>> propertyGot : propertiesWiki.entrySet()) {
            String property = propertyGot.getKey();
            propertyNoValueKB.add(property);
            List<String> values = propertyGot.getValue();
            for (String value : values) {
                String propertyValue = property + "_" + value;
                propertyValueKB.add(propertyValue);
            }
        }

        Integer[] featureVector = new Integer[nbOfFeatures];

        int idx = 0;

        // put 1 if property for entities in Wikidata match with the list of 'data/resource/feature_mapper.csv', otherwise put 0
        for (String propertyNoValue : propertyNoValueFeatureMapper) {
            // search the existance of a certain property in the list of property
            if (propertyNoValueKB.contains(propertyNoValue)){
                featureVector[idx] = 1;
            } else {
                featureVector[idx] = 0;
            }
            idx++;
        }

        // put 1 if property-value for entities in Wikidata match with the list of 'data/resource/feature_mapper.csv', otherwise put 0
        for (String propertyValue : propertyValueFeatureMapper) {
            // search the existance of a certain property-value in the list of property-value
            if (propertyValueKB.contains(propertyValue)) {
                featureVector[idx] = 1;
            } else {
                featureVector[idx] = 0;
            }
            idx++;
        }

        // set information of feature vector
        wikidataElementInfos.setFeatureVector(featureVector);

        return wikidataElementInfos;
    } // end of method 'getFeatureWikidata'


} // end of class FeatureWikidataExtractor
