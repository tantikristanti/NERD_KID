package org.nerd.kid.model;

import au.com.bytecode.opencsv.CSVWriter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.nerd.kid.arff.MainTrainerGenerator;
import org.nerd.kid.data.WikidataElementInfos;
import org.nerd.kid.extractor.ClassExtractor;
import org.nerd.kid.extractor.FeatureWikidataExtractor;
import org.nerd.kid.extractor.wikidata.NerdKBFetcherWrapper;
import org.nerd.kid.extractor.wikidata.WikidataFetcherWrapper;
import org.nerd.kid.service.NerdKidPaths;
import smile.classification.RandomForest;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WikidataNERPredictor {
    private String pathResult = NerdKidPaths.RESULT_CSV;
    private CSVWriter csvWriter = null;

    private XStream streamer = new XStream();

    public RandomForest getForest() {
        return forest;
    }

    public void setForest(RandomForest forest) {
        this.forest = forest;
    }

    private RandomForest forest = null;
    private WikidataFetcherWrapper wrapper;

    public void loadModel() {
        // the model3.xml is located in /src/main/resources
        String pathModel = "/model3.xml";
        InputStream model = this.getClass().getResourceAsStream(pathModel);
        forest = (RandomForest) streamer.fromXML(model);
    }

    public WikidataNERPredictor() {
        XStream.setupDefaultSecurity(streamer);
        streamer.addPermission(AnyTypePermission.ANY);
        loadModel();
    }

    // to initialize the wrapper
    public WikidataNERPredictor(WikidataFetcherWrapper wrapper){
        this();
        this.wrapper = wrapper;
    }

    public String predict(double[] rawFeatures) {
        // if the features are only 0 for all, don't need to predict, the class is UNKNOWN
        double sumOfFeatures = Arrays.stream(rawFeatures).sum();
        if (sumOfFeatures>0) {
            // predict the instance's class based on the features got
            int prediction = forest.predict(rawFeatures);

            // define the name of the class
            List<String> classMapper = new ClassExtractor().loadClasses();
            return classMapper.get(prediction);
        } else {
            return "UNKNOWN";
        }
    }

    public WikidataElementInfos predict(WikidataElementInfos wikiInfos) {
        // get the feature of every instance
        final int length =wikiInfos.getFeatureVector().length;
        double[] rawFeatures = new double[length];
        for (int i = 0; i < length; i++) {
            rawFeatures[i] = ((double) wikiInfos.getFeatureVector()[i]);
        }
        // if the items have the type of "Wikimedia disambiguation page" --> instance of (P31) - Wikimedia disambiguation page (Q4167410)
        // they don't need to be predicted, they are automatically stated as as UKNONWN
        //if (!wikiInfos.getPredictedClass().equals("UNKNOWN")) {
            // if the features are only 0 for all, don't need to predict, the class is UNKNOWN
            double sumOfFeatures = Arrays.stream(rawFeatures).sum();
            if (sumOfFeatures > 0) {
                // predict the instance's class based on the features collected
                int prediction = forest.predict(rawFeatures);

                List<String> classMapper = new ClassExtractor().loadClasses();
                wikiInfos.setPredictedClass(classMapper.get(prediction));
            } else {
                wikiInfos.setPredictedClass("UNKNOWN");
            }
        //}
        return wikiInfos;
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
        // if the items have the type of "Wikimedia disambiguation page" --> instance of (P31) - Wikimedia disambiguation page (Q4167410)
        // they don't need to be predicted, they are automatically stated as as UKNONWN
        //if (!wikidataElement.getPredictedClass().equals("UNKNOWN")){
            // if the features are only 0 for all, they don't need to be predicted; they are stated as UNKNOWN
            double sumOfFeatures = Arrays.stream(rawFeatures).sum();
            if (sumOfFeatures>0) {
                // predict the instance's class based on the features collected
                int prediction = forest.predict(rawFeatures);

                List<String> classMapper = new ClassExtractor().loadClasses();
                wikidataElement.setPredictedClass(classMapper.get(prediction));
            } else {
                wikidataElement.setPredictedClass("UNKNOWN");
            }
        //}
        return wikidataElement;
    }

    public void predictForPreannotation(File fileInput, File fileOutput) throws Exception {
        // get the wikiId and class from the new csv file
        MainTrainerGenerator mainTrainerGenerator = new MainTrainerGenerator();
        List<WikidataElementInfos> inputList = new ArrayList<>();
        inputList = mainTrainerGenerator.extractData(fileInput);
        try {
            csvWriter = new CSVWriter(new FileWriter(fileOutput), ',', CSVWriter.NO_QUOTE_CHARACTER);
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
        System.out.print("Result in " + fileOutput);
    }
}
