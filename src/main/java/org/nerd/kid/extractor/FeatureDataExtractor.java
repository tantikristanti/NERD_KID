package org.nerd.kid.extractor;

import org.nerd.kid.data.WikidataElement;
import org.nerd.kid.data.WikidataElementInfos;
import org.nerd.kid.extractor.grobidNer.WikidataIdClassExtractor;
import org.nerd.kid.extractor.wikidata.WikidataFetcherWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * extract features (properties and values) of WikidataId directly from Wikidata or Nerd knowledge base
 **/
public class FeatureDataExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureDataExtractor.class);

    private WikidataFetcherWrapper wikidataFetcherWrapper = null;

    // for reading feature pattern in feature mapper files in '/resources' directory
    private FeatureFileExtractor featureFileExtractor = new FeatureFileExtractor();

    public FeatureDataExtractor() {
    }

    public FeatureDataExtractor(WikidataFetcherWrapper wikidataFetcherWrapper) {
        this.wikidataFetcherWrapper = wikidataFetcherWrapper;
    }

    public WikidataFetcherWrapper getWikidataFetcherWrapper() {
        return wikidataFetcherWrapper;
    }

    public void setWikidataFetcherWrapper(WikidataFetcherWrapper wikidataFetcherWrapper) {
        this.wikidataFetcherWrapper = wikidataFetcherWrapper;
    }

    public int countFeatureElement() {
        /* count the number of features based on '/resources/feature_mapper.csv'
            and number of features based on the '/resources/feature_mapper_no_value.csv'
         */
        List<String> featuresList = new ArrayList<>();
        List<String> featuresNoValueList = new ArrayList<>();

        int nbOfFeatures = 0; // number of features of feature_mapper.csv
        try {
            featuresList = featureFileExtractor.loadFeatures();
            for (String feature : featuresList) {
                nbOfFeatures++;
            }

            featuresNoValueList = featureFileExtractor.loadFeaturesNoValue();
            for (String feature : featuresNoValueList) {
                nbOfFeatures++;
            }
        } catch (Exception e) {
            LOGGER.info("Some errors encountered when counting number of features.");
        }
        return nbOfFeatures;
    }

    public Double[] getFeatureWikidata(List<String> propertiesNoValue) {
        // count the number of features
        int nbOfFeatures = propertiesNoValue.size();
        int idx = 0;

        // get the features from feature mapper list files
        List<String> featuresNoValueList = featureFileExtractor.loadFeaturesNoValue();
        Double[] featureVector = new Double[featuresNoValueList.size()];

        // put 1 if property for entities in Wikidata match with the list of 'resources/feature_mapper_no_value.csv', otherwise put 0
        for (String propertyNoValue : featuresNoValueList) {
            // search the existance of a certain property in the list of property
            if (propertiesNoValue.contains(propertyNoValue)) {
                featureVector[idx] = 1.0;
            } else {
                featureVector[idx] = 0.0;
            }
            idx++;
        }

        return featureVector;
    }

    public Double[] getFeatureWikidata(Map<String, List<String>> properties) {
        List<String> propertyValueFeatureMapper = featureFileExtractor.loadFeatures();

        Double[] featureVector = new Double[propertyValueFeatureMapper.size()];

        // collect the result of properties-values from the parameter
        List<String> propertyValueKB = new ArrayList<>();
        for (Map.Entry<String, List<String>> propertyGot : properties.entrySet()) {
            String property = propertyGot.getKey();
            List<String> values = propertyGot.getValue();
            // if values for the property exist
            if (values != null) {
                for (String value : values) {
                    String propertyValue = property + "_" + value;
                    propertyValueKB.add(propertyValue);
                }
            }
        }

        int idx = 0;
        // put 1 if property-value for entities in Wikidata match with the list of 'resources/feature_mapper.csv', otherwise put 0
        for (String propertyValue : propertyValueFeatureMapper) {
            // search the existance of a certain property-value in the list of property-value
            if (propertyValueKB.contains(propertyValue)) {
                featureVector[idx] = 1.0;
            } else {
                featureVector[idx] = 0.0;
            }
            idx++;
        }

        return featureVector;
    }

    // method to get wikidataId, label, real-predicted class, and properties in binary format (0-1)
    public WikidataElementInfos getFeatureWikidata(String wikidataId) {
        // count the number of features
        int nbOfFeatures = countFeatureElement();

        // get the element based on the wrapper whether from Wikidata or Nerd API
        WikidataElement wikidataElement = new WikidataElement();
        try {
            wikidataElement = wikidataFetcherWrapper.getElement(wikidataId); // wikidata Id, label, properties-values
        } catch (Exception e) {
            LOGGER.info("Some errors encountered when collecting some features for a Wikidata Id \""+ wikidataId +"\"");
        }
        // get the features from feature mapper list files
        List<String> featuresMap = featureFileExtractor.loadFeatures();
        List<String> featuresNoValueList = featureFileExtractor.loadFeaturesNoValue();

        // set information of id, label, predicted class, features, real class
        WikidataElementInfos wikidataElementInfos = new WikidataElementInfos();
        wikidataElementInfos.setWikidataId(wikidataId);
        wikidataElementInfos.setLabel(wikidataElement.getLabel());

        // properties and values got directly from Wikidata or Nerd API (it depends on the implementation of the WikidataFetcherWrapper interface)
        Map<String, List<String>> propertiesWiki = wikidataElement.getProperties();

        // collect the result of properties-values fetched directly from Wikidata or Nerd KB
        List<String> propertyValueKB = new ArrayList<>();
        List<String> propertyNoValueKB = new ArrayList<>();
        for (Map.Entry<String, List<String>> propertyGot : propertiesWiki.entrySet()) {
            String property = propertyGot.getKey();
            propertyNoValueKB.add(property);
            List<String> values = propertyGot.getValue();
            // if values for the property exist
            if (values != null) {
                for (String value : values) {
                    String propertyValue = property + "_" + value;
                    propertyValueKB.add(propertyValue);
                }
            }
        }

        // the index is based on the number of properties in both feature mapper file
        Double[] featureVector = new Double[nbOfFeatures];

        int idx = 0;

        // put 1 if property for entities in Wikidata match with the list of 'resources/feature_mapper_no_value.csv', otherwise put 0
        for (String propertyNoValue : featuresNoValueList) {
            // search the existance of a certain property in the list of property
            if (propertyNoValueKB.contains(propertyNoValue)) {
                featureVector[idx] = 1.0;
            } else {
                featureVector[idx] = 0.0;
            }
            idx++;
        }

        // put 1 if property-value for entities in Wikidata match with the list of 'resources/feature_mapper.csv', otherwise put 0
        for (String propertyValue : featuresMap) {
            // search the existance of a certain property-value in the list of property-value
            if (propertyValueKB.contains(propertyValue)) {
                featureVector[idx] = 1.0;
            } else {
                featureVector[idx] = 0.0;
            }
            idx++;
        }

        // set information of feature vector
        wikidataElementInfos.setFeatureVector(featureVector);

        return wikidataElementInfos;
    } // end of method 'getFeatureWikidata'

} // end of class FeatureWikidataExtractor
