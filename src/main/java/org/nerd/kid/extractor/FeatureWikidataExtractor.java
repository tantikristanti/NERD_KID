package org.nerd.kid.extractor;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.lang3.ArrayUtils;
import org.nerd.kid.data.WikidataElement;
import org.nerd.kid.data.WikidataElementInfos;
import org.nerd.kid.exception.NerdKidException;
import org.nerd.kid.extractor.wikidata.WikidataFetcherWrapper;

import java.io.*;
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
    private WikidataIdClassExtractor wikidataIdClassExtractor = new WikidataIdClassExtractor();

    private List<WikidataElementInfos> featureMatrix = new ArrayList<>();
    private String path = "data/csv/MatrixFeatureWikidata.csv";

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
        WikidataElement wikidataElement = null;
        try {
            wikidataElement = wikidataFetcherWrapper.getElement(wikidataId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // set information of id, label, predicted class, features, real class
        WikidataElementInfos wikidataElementInfos = new WikidataElementInfos();
        wikidataElementInfos.setWikidataId(wikidataId);
        wikidataElementInfos.setLabel(wikidataElement.getLabel());

        // properties and values got directly from Wikidata
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
        // get the list of properties-values based on the result directly from Wikidata
        List<String> propertyValueWikidata = new ArrayList<>();
        List<String> propertyNoValueWikidata = new ArrayList<>();
        for (Map.Entry<String, List<String>> propertyGot : propertiesWiki.entrySet()) {
            String property = propertyGot.getKey();
            propertyNoValueWikidata.add(property);
            List<String> values = propertyGot.getValue();
            for (String value : values) {
                String propertyValue = property + "_" + value;
                propertyValueWikidata.add(propertyValue);
            }
        }

        Integer[] featureVector = new Integer[nbOfFeatures];

        int idx = 0;

        // put 1 if property for entities in Wikidata match with the list of 'data/resource/feature_mapper.csv', otherwise put 0
        for (String propertyNoValue : propertyNoValueFeatureMapper) {
            if (propertyNoValueWikidata.contains(propertyNoValue)){
                featureVector[idx] = 1;
            } else {
                featureVector[idx] = 0;
            }
            idx++;
        }

        // put 1 if property-value for entities in Wikidata match with the list of 'data/resource/feature_mapper.csv', otherwise put 0
        for (String propertyValue : propertyValueFeatureMapper) {
            if (propertyValueWikidata.contains(propertyValue)) {
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


    // method to get the wikdiataId, label, real-predicted class, and raw properties for each wikidataId directly from Wikidata KB
    public WikidataElementInfos getRawFeatureWikidata(String wikidataId) {
        // get the element based on the wrapper whether from Wikidata or Nerd KB
        WikidataElement wikidataElement = null;
        try {
            wikidataElement = wikidataFetcherWrapper.getElement(wikidataId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // set information of id, label, predicted class, features, real class
        WikidataElementInfos wikidataElementInfos = new WikidataElementInfos();
        wikidataElementInfos.setWikidataId(wikidataId);
        wikidataElementInfos.setLabel(wikidataElement.getLabel());

        // properties and values got directly from Wikidata
        Map<String, List<String>> propertiesWiki = wikidataElement.getProperties();
        List<String> propertiesNoValueWiki = wikidataElement.getPropertiesNoValue();

        // get the list of properties-values based on the result directly from Wikidata
        List<String> propertyValueWikidata = new ArrayList<>();
        for (Map.Entry<String, List<String>> propertyGot : propertiesWiki.entrySet()) {
            String property = propertyGot.getKey();
            propertyValueWikidata.add(property);
            List<String> values = propertyGot.getValue();
            for (String value : values) {
                String propertyValue = property + "_" + value;
                propertyValueWikidata.add(propertyValue);
            }
        }

        // set information of raw feature vector
        wikidataElementInfos.setRawFeatureVector(propertyValueWikidata);

        return wikidataElementInfos;
    }

    public void saveFeatureWikidata(List<WikidataElementInfos> matrix) throws Exception {

        CSVWriter csvWriter = new CSVWriter(new FileWriter(path, true), ',', CSVWriter.NO_QUOTE_CHARACTER);
        CSVReader csvReader = new CSVReader(new FileReader(path));

        try {

            // if header doesn't exist yet, add one
            if (csvReader.readNext() == null) {

                Map<String, List<String>> featuresMap = featureFileExtractor.loadFeatures();
                List<String> featuresNoValueMap = featureFileExtractor.loadFeaturesNoValue();

                String[] headerMain = {"WikidataID,LabelWikidata,RealClass,PredictedClass"};
                List<String> headerProperties = new ArrayList<>();

                for (String propertyNoValueGot : featuresNoValueMap) {
                    headerProperties.add(propertyNoValueGot);
                }

                for (Map.Entry<String, List<String>> propertyGot : featuresMap.entrySet()) {
                    String property = propertyGot.getKey();
                    List<String> values = propertyGot.getValue();
                    for (String value : values) {
                        String propertyValue = property + "_" + value;
                        headerProperties.add(propertyValue);
                    }
                }

                String[] header = (String[]) ArrayUtils.addAll(headerMain, headerProperties.toArray());
                csvWriter.writeNext(header);
            }

            Map<String, String> matrixIdClassWiki = wikidataIdClassExtractor.loadIdClass(new FileInputStream(path));
            List<String> idWikis = new ArrayList<>();
            for (Map.Entry<String, String> entry : matrixIdClassWiki.entrySet()) {
                idWikis.add(entry.getKey());
            }
            for (WikidataElementInfos item : matrix) {
                // just add wikidata IDs that don't exist yet
                if (!idWikis.contains(item.getWikidataId())) {
                    String[] dataWikidata = {item.getWikidataId(), item.getLabel(), item.getRealClass(), item.getPredictedClass()};
                    Integer[] properties = item.getFeatureVector();
                    List<String> dataProperties = new ArrayList<>();
                    for (Integer property : properties) {
                        dataProperties.add(property.toString());
                    }
                    String[] data = (String[]) ArrayUtils.addAll(dataWikidata, dataProperties.toArray());
                    csvWriter.writeNext(data);
                } else {
                    System.out.println("Data exists already.");
                }
            }
            csvReader.close();
            csvWriter.flush();
            csvWriter.close();
        } catch (Exception e) {
            throw new NerdKidException("An exception occured while saving or accessing data.", e);
        }
    } // end of method 'saveFeatureWikidata'

} // end of class FeatureWikidataExtractor
