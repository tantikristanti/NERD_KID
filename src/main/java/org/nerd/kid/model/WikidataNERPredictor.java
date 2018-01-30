package org.nerd.kid.model;

import au.com.bytecode.opencsv.CSVWriter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.nerd.kid.arff.MainTrainerGenerator;
import org.nerd.kid.data.WikidataElementInfos;
import org.nerd.kid.extractor.ClassExtractor;
import org.nerd.kid.extractor.FeatureWikidataExtractor;
import org.nerd.kid.extractor.wikidata.NerdKBFetcherWrapper;
import org.nerd.kid.extractor.wikidata.WikibaseWrapper;
import org.nerd.kid.extractor.wikidata.WikidataFetcherWrapper;
import smile.classification.RandomForest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WikidataNERPredictor {
    private CSVWriter csvWriter = null;

    private XStream streamer = new XStream();
    private RandomForest forest = null;

    public void loadModel() {
        // the model.xml is located in /src/main/resources
        InputStream model = this.getClass().getResourceAsStream("/model.xml");
        forest = (RandomForest) streamer.fromXML(model);
    }

    public WikidataNERPredictor() {
        XStream.setupDefaultSecurity(streamer);
        streamer.addPermission(AnyTypePermission.ANY);
        loadModel();
    }

    public WikidataElementInfos predict(String wikidataId) {
        // extract the characteristics of entities from Nerd
        WikidataFetcherWrapper wrapper = new NerdKBFetcherWrapper();
        FeatureWikidataExtractor extractor = new FeatureWikidataExtractor(wrapper);
        final WikidataElementInfos wikidataElement = extractor.getFeatureWikidata(wikidataId);

        // get the feature of every instance
        final int length = wikidataElement.getFeatureVector().length;
        double[] rawFeatures = new double[length];
        for (int i = 0; i < length; i++) {
            rawFeatures[i] = ((double) wikidataElement.getFeatureVector()[i]);
        }

        // predict the instance's class based on the features collected
        int prediction = forest.predict(rawFeatures);

        List<String> classMapper = new ClassExtractor().loadClasses();
        wikidataElement.setPredictedClass(classMapper.get(prediction));

        return wikidataElement;
    }

    public void predict() throws Exception {
        try {
            predict(new File("data/csv/NewElements.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void predict(File file) throws Exception {
        String csvDataPath = "result/csv/ResultPredictedClass.csv";

        // get the wikiId and class from the new csv file
        MainTrainerGenerator mainTrainerGenerator = new MainTrainerGenerator();
        List<WikidataElementInfos> inputList = new ArrayList<>();
        inputList = mainTrainerGenerator.extractData(file);
        try {
            csvWriter = new CSVWriter(new FileWriter(csvDataPath), ',', CSVWriter.NO_QUOTE_CHARACTER);
            // header's file
            String[] headerPredict = {"WikidataID,LabelWikidata,Class"};
            csvWriter.writeNext(headerPredict);
            for (WikidataElementInfos wikiElement : inputList) {
                // get the prediction result of every wikidata Id in the csv file
                String resultPredict = predict(wikiElement.getWikidataId()).getPredictedClass();

                // get the label of every wikidata Id in the csv file
                WikidataFetcherWrapper wrapper = new NerdKBFetcherWrapper();
                FeatureWikidataExtractor extractor = new FeatureWikidataExtractor(wrapper);
                final WikidataElementInfos wikidataElement = extractor.getFeatureWikidata(wikiElement.getWikidataId());
                String label = wikidataElement.getLabel();

                // write the result into a new csv file
                String[] dataPredict = {wikiElement.getWikidataId(),label,resultPredict};
                csvWriter.writeNext(dataPredict);
            }

        } finally {
            csvWriter.flush();
            csvWriter.close();
        }
        System.out.print("Result in 'result/csv/ResultPredictedClass.csv'");
    }

    public void predictedResultAndProperties() throws Exception {
        try {
            predictedResultAndProperties(new File("data/csv/NewElementsOriginal.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void predictedResultAndProperties(File file) throws Exception {
        // extract the characteristics of entities from Wikidata
        WikidataFetcherWrapper wrapper = new WikibaseWrapper();
        FeatureWikidataExtractor extractor = new FeatureWikidataExtractor(wrapper);

        String csvDataPath = "result/csv/ResultPredictedClassProperties.csv";

        // get the wikiId and class from the new csv file
        MainTrainerGenerator mainTrainerGenerator = new MainTrainerGenerator();
        List<WikidataElementInfos> inputList = new ArrayList<>();
        inputList = mainTrainerGenerator.extractData(file);
        try {
            csvWriter = new CSVWriter(new FileWriter(csvDataPath), ',', CSVWriter.NO_QUOTE_CHARACTER);
            // header's file
            String[] headerPredict = {"WikidataID,LabelWikidata,Class,Property_Value"};

            csvWriter.writeNext(headerPredict);
            for (WikidataElementInfos wikiElement : inputList) {
                // predict every wikidata Id in the file
                String resultPredict = predict(wikiElement.getWikidataId()).getPredictedClass();

                // get the label of every wikidata Id in the csv file
                final WikidataElementInfos wikidataElement = extractor.getRawFeatureWikidata(wikiElement.getWikidataId());
                String label = wikidataElement.getLabel();

                // get the raw properties of each wikidataId from Wikidata KB
                List<String> propertyValueWikidata = extractor.getRawFeatureWikidata(wikiElement.getWikidataId()).getRawFeatureVector();
                String propertyValueJoined = null;
                for(String property : propertyValueWikidata) {
                    propertyValueJoined = String.join("-", property);
                }
                // write the result into a new csv file
                String[] dataPredict = {wikiElement.getWikidataId(),label,resultPredict,propertyValueJoined};
                csvWriter.writeNext(dataPredict);
            }

        } finally {
            csvWriter.flush();
            csvWriter.close();
        }
        System.out.print("Result in 'result/csv/ResultPredictedClassProperties.csv'");

    }
}
