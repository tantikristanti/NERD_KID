package org.nerd.kid.extractor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.nerd.kid.data.WikidataElement;
import org.nerd.kid.data.WikidataElementInfos;
import org.nerd.kid.rest.DataPredictor;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* extract features (properties and values) of WikidataId directly from Wikidata knowledge base
* */

public class FeatureWikidataExtractor {

    private WikidataFetcherWrapper wikidataFetcherWrapper;

    DataPredictor predictData = new DataPredictor();

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

        // count the number of features
        int nbOfFeatures = 0;
        Map<String, List<String>> featuresMap = loadFeatures();
        for (String key : featuresMap.keySet()) {
            nbOfFeatures += featuresMap.get(key).size();
        }

        // get the feature of Wikidata for each wikidataId and class found in input file csv
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

            Integer[] featureVector = new Integer[nbOfFeatures];

            int idx = 0;
            // properties based on the list of mapper_feature.csv
            for (String allowedProperty : featuresMap.keySet()) {

                // properties gathered directly from Wikidata
                Map<String, List<String>> propertiesWiki = wikidataElement.getProperties();

                /* if property is not null, get the value from the feature_mapper and also directly from API's Wikidata
                 then put it in the list
                 if both of this list contain the same property-values, give the value 1
                 */
                if (propertiesWiki.get(allowedProperty) != null) {
                    List<String> propertyValuesInWikidataFetchedObject = propertiesWiki.get(allowedProperty);
                    List<String> allowedValues = featuresMap.get(allowedProperty);
                    for (String allowedValue : allowedValues) {
                        if (propertyValuesInWikidataFetchedObject.contains(allowedValue)) {
                            featureVector[idx] = 1;
                        } else {
                            featureVector[idx] = 0;
                        }
                        idx++;
                    }
                }
            }

            // set information of feature vector
            wikidataElementInfos.setFeatureVector(featureVector);

            // set null for predicted class
            wikidataElementInfos.setPredictedClass("Null");

            featureMatrix.add(wikidataElementInfos);

        } // end of looping to read file that contains Wikidata Id and class

        return featureMatrix;
    } // end of method getFeatureWikidata

//
//            // add another data column needed for testing file
//            for (int j = 0; j < colNumberNewData; j++) {
//                if (j == 0) { // first column : Wikidata Id
//                    matrixNewData[i][j] = elementWikiId;
//                } else if (j == 1) { //second column : label of Wikidata Id
//                    matrixNewData[i][j] = labelWiki.get(i);
//                } else if (j == 2) { // third column : predicted data
//                    matrixNewData[i][j] = "Null";
//                } else if (j == lastColumn) { // last column : Nerd's class
//                    matrixNewData[i][j] = (dataCSVClass.get(i) == "" ? "Null" : dataCSVClass.get(i));
//                }
//
//            } // end of column of matrix
//        } // end of row of matrix
//
//        // get the result of prediction and put it in matrix new data
//        String[] resultPredict = predictData.predictNewTestData(testX);
//        for (int i = 0; i < rowNumber; i++) {
//            matrixNewData[i][2] = resultPredict[i];
//        }
//
//        // add properties of WikidataId in matrix of new data
//        for (int i = 0; i < rowNumber; i++) {
//            int col = 3; // properties start from column 3 until the size of properties
//
//            // as the size of properties for each Wikidata Id
//            for (int j = 0; j < colNumberTestX; j++) {
//                matrixNewData[i][col] = String.valueOf((int) testX[i][j]);
//                col++;
//            }
//        }
//
//
//        return matrixNewData;
//
//    } // end of method getFeatureWikidata

    public Map<String, List<String>> loadFeatures() throws Exception {
        return loadFeatures(new FileInputStream("data/resource/feature_mapper.csv"));
    }

    public Map<String, List<String>> loadFeatures(InputStream inputStreamFeatureFile) throws IOException {
        Map<String, List<String>> featureMap = new HashMap<>();
        Reader featureMapperIn = new InputStreamReader(inputStreamFeatureFile);
        Iterable<CSVRecord> recordsFeatures = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(featureMapperIn);

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

    public void printWikidataFeatures(Map<String, List<String>> result) {
        for (Map.Entry<String, List<String>> entry : result.entrySet()) {
            System.out.println(entry.getKey() + ": ");
            List<String> values = entry.getValue();
            for (String item : values) {
                System.out.println("\t" + item);
            }
        }
    }

} // end of class FeatureWikidataExtractor
