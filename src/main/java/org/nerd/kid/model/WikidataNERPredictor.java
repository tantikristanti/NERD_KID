package org.nerd.kid.model;

import au.com.bytecode.opencsv.CSVWriter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.apache.commons.lang3.ArrayUtils;
import org.nerd.kid.arff.TrainerGenerator;
import org.nerd.kid.data.WikidataElement;
import org.nerd.kid.data.WikidataElementInfos;
import org.nerd.kid.extractor.ClassExtractor;
import org.nerd.kid.extractor.FeatureDataExtractor;
import org.nerd.kid.extractor.wikidata.NerdKBFetcherWrapper;
import org.nerd.kid.extractor.wikidata.WikidataFetcherWrapper;
import org.nerd.kid.service.NerdKidPaths;
import smile.classification.RandomForest;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

public class WikidataNERPredictor {
    private CSVWriter csvWriter = null;
    private XStream streamer = new XStream();
    private RandomForest forest = null;
    private WikidataFetcherWrapper wrapper = null;
    private FeatureDataExtractor featureDataExtractor = null;
    private ModelBuilder modelBuilder = new ModelBuilder();

    public WikidataNERPredictor() {
        String pathModelZip = "model.zip";
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            XStream.setupDefaultSecurity(streamer);
            streamer.addPermission(AnyTypePermission.ANY);
            InputStream modelStream = modelBuilder.readZipFile(new File(classLoader.getResource(pathModelZip).getFile()));
            loadModel(modelStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public WikidataNERPredictor() {
//        XStream.setupDefaultSecurity(streamer);
//        streamer.addPermission(AnyTypePermission.ANY);
//        loadModel();
//    }

    public RandomForest getForest() {
        return forest;
    }

    public void setForest(RandomForest forest) {
        this.forest = forest;
    }

    // loading model in Xml format
//    public void loadModel() {
//        String pathModel = "/model.xml";
//        try {
//            // the model.xml is located in /src/main/resources
//            InputStream model = this.getClass().getResourceAsStream(pathModel);
//            forest = (RandomForest) streamer.fromXML(model);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    // loading model in Inputstream format --> after decompressing with GzipInputStream
    public void loadModel(InputStream modelStream) {
        try {
            forest = (RandomForest) streamer.fromXML(modelStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // to initialize the wrapper
    public WikidataNERPredictor(WikidataFetcherWrapper wrapper) {
        this();
        this.wrapper = wrapper;
    }

    /* Method for accepting Wikidata element (id, label, properties-values) to be predicted*/
    public WikidataElementInfos predict(WikidataElement wikidataElement) {
        featureDataExtractor = new FeatureDataExtractor();

        final WikidataElementInfos wikidataElementInfos = new WikidataElementInfos();

        // fill the wikidata element with basic infos (id, label)
        wikidataElementInfos.setWikidataId(wikidataElement.getId());
        wikidataElementInfos.setLabel(wikidataElement.getLabel());

        // collect the properties information in a list and a map
        List<String> propertiesNoValue = wikidataElement.getPropertiesNoValue();
        Map<String, List<String>> properties = wikidataElement.getProperties();

        /* convert the properties information into the format binary 0-1 if they are found in the feature mapper files
        since Smile can only predict with the type of Array in double, then the results are already converted into the proper type double[]
        */

        Double[] resultGetFeatureWikidataPropertiesNoValue = featureDataExtractor.getFeatureWikidata(propertiesNoValue);
        Double[] resultGetFeatureWikidataProperties = featureDataExtractor.getFeatureWikidata(properties);

        // combine the result of features collected
        Double[] combinedFeatureWikidata = Stream.concat(Arrays.stream(resultGetFeatureWikidataPropertiesNoValue), Arrays.stream(resultGetFeatureWikidataProperties)).toArray(Double[]::new);

        // if the features are only 0 for all, they don't need to be predicted; they are stated as UNKNOWN
        double sumOfFeatures = Arrays.stream(ArrayUtils.toPrimitive(combinedFeatureWikidata)).sum();
        if (sumOfFeatures > 0) {
            // predict the instance's class based on the features collected
            int prediction = forest.predict(ArrayUtils.toPrimitive(combinedFeatureWikidata));

            List<String> classMapper = new ClassExtractor().loadClasses();
            wikidataElementInfos.setPredictedClass(classMapper.get(prediction));
        } else {
            wikidataElementInfos.setPredictedClass("UNKNOWN");
        }
        //}
        return wikidataElementInfos;
    }

    // get the input of wikidata element infos and retur the result of prediction
    public WikidataElementInfos predict(WikidataElementInfos wikiInfos) {
        // get the feature of every instance
        final int length = wikiInfos.getFeatureVector().length;
        double[] rawFeatures = new double[length];
        for (int i = 0; i < length; i++) {
            rawFeatures[i] = ((double) wikiInfos.getFeatureVector()[i]);
        }

        // if the features are only 0 for all, they don't need to be predicted; they are stated as UNKNOWN
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

    // get the input of Wikidata Id and return the prediction result
    public WikidataElementInfos predict(String wikidataId) {
        // extract the characteristics of entities from Nerd
        WikidataFetcherWrapper wrapper = new NerdKBFetcherWrapper();
        FeatureDataExtractor extractor = new FeatureDataExtractor(wrapper);
        final WikidataElementInfos wikidataElementInfos = extractor.getFeatureWikidata(wikidataId);

        // get the feature of every instance
        final int length = wikidataElementInfos.getFeatureVector().length;
        double[] rawFeatures = new double[length];
        for (int i = 0; i < length; i++) {
            // convert feature to double for Smile can predict it
            rawFeatures[i] = ((double) wikidataElementInfos.getFeatureVector()[i]);
        }

        // if the features are only 0 for all, they don't need to be predicted; they are stated as UNKNOWN
        double sumOfFeatures = Arrays.stream(rawFeatures).sum();
        if (sumOfFeatures > 0) {
            // predict the instance's class based on the features collected
            int prediction = forest.predict(rawFeatures);

            List<String> classMapper = new ClassExtractor().loadClasses();
            // set the class with the prediction result
            wikidataElementInfos.setPredictedClass(classMapper.get(prediction));
        } else {
            wikidataElementInfos.setPredictedClass("UNKNOWN");
        }
        return wikidataElementInfos;
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
                FeatureDataExtractor extractor = new FeatureDataExtractor(wrapper);
                final WikidataElementInfos wikidataElement = extractor.getFeatureWikidata(wikiElement.getWikidataId());
                String label = wikidataElement.getLabel();

                // write the result into a new csv file
                String[] dataPredict = {wikiElement.getWikidataId(), label, resultPredict};
                csvWriter.writeNext(dataPredict);
            }

        } finally {
            csvWriter.flush();
            csvWriter.close();
        }
        System.out.print("Result in " + fileOutput);
    }

    public static void main(String[] args) throws Exception {
//        String fileInput = NerdKidPaths.DATA_CSV + "/NewElements.csv";
//        String fileOutput = NerdKidPaths.RESULT_CSV + "/ResultPredictedClass.csv";
//
//        WikidataNERPredictor wikidataNERPredictor = new WikidataNERPredictor();
//        System.out.println("Processing the pre-annotation ...");
//        wikidataNERPredictor.predictForPreannotation(new File(fileInput), new File(fileOutput));


        WikidataNERPredictor wikidataNERPredictor = new WikidataNERPredictor();
        WikidataElement wikidataElement = new WikidataElement();
        wikidataElement.setId("Q1011"); // Cape Verde (Class: LOCATION)
        List<String> propertiesNoValue = Arrays.asList("P1566","P30","P36");
        Map<String,List<String>> properties = new HashMap<>();
        properties.put("P31",Arrays.asList("Q6256"));
        wikidataElement.setProperties(properties);
        wikidataElement.setPropertiesNoValue(propertiesNoValue);
        List<String> tempPropertiesNoValue = wikidataElement.getPropertiesNoValue();
        for (String element : tempPropertiesNoValue){
            System.out.println(element);
        }

        Map<String, List<String>> tempProperties = wikidataElement.getProperties();
        for (Map.Entry<String, List<String>> element : tempProperties.entrySet()){
            System.out.println(element.getKey());
            List<String> vals = element.getValue();
            for (String val : vals){
                System.out.println(val);
            }
        }

        WikidataElementInfos result = wikidataNERPredictor.predict(wikidataElement);

        System.out.println("Class predicted: " + result.getPredictedClass());
    }
}
