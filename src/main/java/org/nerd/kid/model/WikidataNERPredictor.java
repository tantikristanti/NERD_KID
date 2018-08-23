package org.nerd.kid.model;

import au.com.bytecode.opencsv.CSVWriter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.nerd.kid.arff.TrainerGenerator;
import org.nerd.kid.data.WikidataElementInfos;
import org.nerd.kid.extractor.ClassExtractor;
import org.nerd.kid.extractor.FeatureWikidataExtractor;
import org.nerd.kid.extractor.wikidata.NerdKBFetcherWrapper;
import org.nerd.kid.extractor.wikidata.WikidataFetcherWrapper;
import org.nerd.kid.service.NerdKidPaths;
import smile.classification.RandomForest;

import java.io.*;
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

    ModelBuilder modelBuilder = new ModelBuilder();

    public WikidataNERPredictor() {
        String pathModelZip = "model.zip";
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            XStream.setupDefaultSecurity(streamer);
            streamer.addPermission(AnyTypePermission.ANY);
            InputStream modelStream = modelBuilder.readZipFile(new File(classLoader.getResource(pathModelZip).getFile()));
            loadModel(modelStream);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // loading model in Xml format
    public void loadModel() {
        String pathModel = "/model.xml";
        try {
            // the model.xml is located in /src/main/resources
            InputStream model = this.getClass().getResourceAsStream(pathModel);
            forest = (RandomForest) streamer.fromXML(model);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // loading model in Inputstream format --> after decompressing with GzipInputStream
    public void loadModel(InputStream modelStream) {
        try {
            forest = (RandomForest) streamer.fromXML(modelStream);
        }catch (Exception e){
            e.printStackTrace();
        }
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
        TrainerGenerator trainerGenerator = new TrainerGenerator();
        List<WikidataElementInfos> inputList = new ArrayList<>();
        inputList = trainerGenerator.extractData(fileInput);
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

    public static void main(String[] args) throws Exception{
        String fileInput = NerdKidPaths.DATA_CSV + "/NewElements.csv";
        String fileOutput = NerdKidPaths.RESULT_CSV + "/ResultPredictedClass.csv";

        WikidataNERPredictor wikidataNERPredictor = new WikidataNERPredictor();
        System.out.println("Processing the pre-annotation ...");
        wikidataNERPredictor.predictForPreannotation(new File(fileInput), new File(fileOutput));
    }
}
