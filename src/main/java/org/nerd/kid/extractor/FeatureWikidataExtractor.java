package org.nerd.kid.extractor;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.ArrayUtils;
import org.nerd.kid.data.WikidataElement;
import org.nerd.kid.data.WikidataElementInfos;
import org.nerd.kid.exception.NerdKidException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
* extract features (properties and values) of WikidataId directly from Wikidata knowledge base
* */

public class FeatureWikidataExtractor {
    private WikidataFetcherWrapper wikidataFetcherWrapper = new WikidataFetcherWrapper();
    private FeatureFileExtractor featureFileExtractor = new FeatureFileExtractor();
    private WikidataIdClassExtractor wikidataIdClassExtractor = new WikidataIdClassExtractor();

    private List<WikidataElementInfos> featureMatrix = new ArrayList<>();
    private String path = "data/csv/MatrixFeatureWikidata.csv";

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

        // count the number of features based on 'data/resource/feature_mapper.csv'
        int nbOfFeatures = 0;
        Map<String, List<String>> featuresMap = featureFileExtractor.loadFeatures();
        for (String key : featuresMap.keySet()) {
            nbOfFeatures += featuresMap.get(key).size();
        }

        Reader reader = new FileReader(inputFile);
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

            // set null for predicted class
            wikidataElementInfos.setPredictedClass("Null");

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
                } else {
                    featureVector[idx] = 0;
                }
                idx++;
            }

            // set information of feature vector
            wikidataElementInfos.setFeatureVector(featureVector);

            featureMatrix.add(wikidataElementInfos);

        } // end of looping to read file that contains Wikidata Id and class

        return featureMatrix;
    } // end of method getFeatureWikidata

    public void saveFeatureWikidata(List<WikidataElementInfos> matrix) throws Exception {

        CSVWriter csvWriter = new CSVWriter(new FileWriter(path, true), ',', CSVWriter.NO_QUOTE_CHARACTER);
        CSVReader csvReader = new CSVReader(new FileReader(path));

        try {

            // if header doesn't exist yet, add one
            if (csvReader.readNext() == null) {

                Map<String, List<String>> featuresMap = featureFileExtractor.loadFeatures();

                String[] headerMain = {"WikidataID,LabelWikidata,RealClass,PredictedClass"};
                List<String> headerProperties = new ArrayList<>();
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
    }

} // end of class FeatureWikidataExtractor
