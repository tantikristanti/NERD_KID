package org.nerd.kid.extractor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeatureFileExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureFileExtractor.class);

    public List<String> loadFeatures() {
        // get the features (properties and values) from the list in the csv file
        //String fileFeatureMapper = pathSource + "/feature_mapper.csv";

        String fileFeatureMapper = "/feature_mapper.csv";
        InputStream inputStream = this.getClass().getResourceAsStream(fileFeatureMapper);
        //ClassLoader classLoader = getClass().getClassLoader();

        try {
            //File file = new File(classLoader.getResource(fileFeatureMapper).getFile());
            //InputStream inputStream = new FileInputStream(file);

//            Map<String, List<String>> featureMap = new HashMap<>();
            List<String> featureList = new ArrayList<>();
            Reader featureMapperIn = new InputStreamReader(inputStream);
            Iterable<CSVRecord> recordsFeatures = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(featureMapperIn);

            for (CSVRecord recordFeature : recordsFeatures) {
                String property = recordFeature.get("Property");
                String value = recordFeature.get("Value");
                String propertyValue = property+"_"+value;

                // in order to get unique of property-value combination
//                if (featureMap.keySet().contains(property)) {
//                    featureMap.get(property).add(value); // if a property-value exists, get it
//                } else {
//                    List<String> values = new ArrayList<>();
//                    values.add(value);
//                    featureMap.put(property, values); // if they don't exist yet, add a new one
//                }

                if (property != null) {
                    featureList.add(propertyValue);
                }
            }
            return featureList;
        } catch (IOException e) {
            LOGGER.info("Some errors encountered when loading some features from file in \""+ fileFeatureMapper + "\"", e);
            return new ArrayList<>();
        }
    }

    public List<String> loadFeaturesNoValue() {
        // get the features (properties) from the list in the csv file
        //String fileFeatureMapperNoValue = pathSource + "/feature_mapper_no_value.csv";

        String fileFeatureMapper = "/feature_mapper_no_value.csv";
        InputStream inputStream = this.getClass().getResourceAsStream(fileFeatureMapper);
        //ClassLoader classLoader = getClass().getClassLoader();

        try {
            //File file = new File(classLoader.getResource(fileFeatureMapper).getFile());
            //InputStream inputStream = new FileInputStream(file);
            List<String> featureListNoValue = new ArrayList<>();
            Reader featureMapperIn = new InputStreamReader(inputStream);
            Iterable<CSVRecord> recordsFeaturesNoValue = null;
            recordsFeaturesNoValue = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(featureMapperIn);

            for (CSVRecord recordFeatureNoValue : recordsFeaturesNoValue) {
                String property = recordFeatureNoValue.get("Property");

                if (property != null) {
                    featureListNoValue.add(property);
                }
            }

            return featureListNoValue;
        } catch (IOException e) {
            LOGGER.info("Some errors encountered when loading some features in \""+ fileFeatureMapper + "\"", e);
            return new ArrayList<>();
        }
    }
}
