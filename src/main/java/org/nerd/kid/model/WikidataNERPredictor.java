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
import org.nerd.kid.extractor.FeatureFileExtractor;
import org.nerd.kid.extractor.wikidata.NerdKBFetcherWrapper;
import org.nerd.kid.extractor.wikidata.WikidataFetcherWrapper;
import org.nerd.kid.service.NerdKidPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.classification.RandomForest;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

public class WikidataNERPredictor {
    private static final Logger LOGGER = LoggerFactory.getLogger(WikidataNERPredictor.class);

    private CSVWriter csvWriter = null;
    private XStream streamer = new XStream();
    private RandomForest forest = null;
    private WikidataFetcherWrapper wrapper = null;
    private FeatureDataExtractor featureDataExtractor = null;
    private ModelBuilder modelBuilder = new ModelBuilder();

    public void init() {
        String pathModelZip = "model.zip";
        try {
            XStream.setupDefaultSecurity(streamer);
            streamer.addPermission(AnyTypePermission.ANY);
            InputStream modelStream = modelBuilder.readZipFile(this.getClass().getResourceAsStream(pathModelZip));
            loadModel(modelStream);
        } catch (Exception e) {
            LOGGER.info("Some errors encountered when loading a model in \""+ pathModelZip + "\"", e);
        }
    }

    // constructor for loading model in XML format
/*    public WikidataNERPredictor() {
        XStream.setupDefaultSecurity(streamer);
        streamer.addPermission(AnyTypePermission.ANY);
        loadModel();
    }

     loading model in Xml format
    public void loadModel() {
        String pathModel = "/model.xml";
        try {
            // the model.xml is located in /src/main/resources
            InputStream model = this.getClass().getResourceAsStream(pathModel);
            forest = (RandomForest) streamer.fromXML(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public RandomForest getForest() {
        return forest;
    }

    public void setForest(RandomForest forest) {
        this.forest = forest;
    }

    // loading model in Inputstream format --> after decompressing with GzipInputStream
    public void loadModel(InputStream modelStream) {
        try {
            forest = (RandomForest) streamer.fromXML(modelStream);
        } catch (Exception e) {
            LOGGER.info("Some errors encountered when loading a stream of model in \""+ modelStream + "\"", e);
        }
    }

    // to initialize the wrapper
    public WikidataNERPredictor(WikidataFetcherWrapper wrapper) {
        init();
        this.wrapper = wrapper;
    }

    /* Method for accepting Wikidata element (id, label, properties-values) to be predicted*/
    public WikidataElementInfos predict(WikidataElement wikidataElement) {
        // feature data extractor doesn't depend on any wrapper, accepting the wikidata element object
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

        // combine all the features collected (with and without values)
        Double[] combinedFeatureWikidata = Stream.concat(Arrays.stream(resultGetFeatureWikidataPropertiesNoValue), Arrays.stream(resultGetFeatureWikidataProperties)).toArray(Double[]::new);

        // if the features are only 0 for all, they don't need to be predicted; they are stated as OTHER
        // OTHER might be entity but recognized as UNKNOWN class type or it might be not entity)
        double sumOfFeatures = Arrays.stream(ArrayUtils.toPrimitive(combinedFeatureWikidata)).sum();
        if (sumOfFeatures > 0) {
            // predict the instance's class based on the features collected
            int prediction = forest.predict(ArrayUtils.toPrimitive(combinedFeatureWikidata));

            List<String> classMapper = new ClassExtractor().loadClasses();
            wikidataElementInfos.setPredictedClass(classMapper.get(prediction));
        } else {
            wikidataElementInfos.setPredictedClass("OTHER");
        }
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

        // if the features are only 0 for all, they don't need to be predicted; they are stated as OTHER
        double sumOfFeatures = Arrays.stream(rawFeatures).sum();
        if (sumOfFeatures > 0) {
            // predict the instance's class based on the features collected
            int prediction = forest.predict(rawFeatures);

            List<String> classMapper = new ClassExtractor().loadClasses();
            wikiInfos.setPredictedClass(classMapper.get(prediction));
        } else {
            wikiInfos.setPredictedClass("OTHER");
        }
        //}
        return wikiInfos;
    }

    // get the input of Wikidata Id and return the prediction result
    public WikidataElementInfos predict(String wikidataId) {
        FeatureDataExtractor extractor;
        WikidataElementInfos wikidataElementInfos = new WikidataElementInfos();
        try {
            // extract the characteristics of entities from Nerd
            extractor = new FeatureDataExtractor(wrapper);
            wikidataElementInfos = extractor.getFeatureWikidata(wikidataId);

            // get the feature of every instance
            final int length = wikidataElementInfos.getFeatureVector().length;
            // if there is at least 1 feature
            if (wikidataElementInfos.getFeatureVector() != null) {
                double[] rawFeatures = new double[length];
                for (int i = 0; i < length; i++) {
                    // convert feature to double for Smile can predict it
                    rawFeatures[i] = ((double) wikidataElementInfos.getFeatureVector()[i]);
                }
                // if the features are only 0 for all, they don't need to be predicted; they are stated as OTHER
                double sumOfFeatures = Arrays.stream(rawFeatures).sum();
                if (sumOfFeatures > 0) {
                    // predict the instance's class based on the features collected
                    int prediction = forest.predict(rawFeatures);

                    List<String> classMapper = new ClassExtractor().loadClasses();
                    // set the class with the prediction result
                    wikidataElementInfos.setPredictedClass(classMapper.get(prediction));
                } else {
                    wikidataElementInfos.setPredictedClass("OTHER");
                }
            }
        }catch (RuntimeException e){
            LOGGER.info("Some errors encountered when collecting some features for predicting a Wikidata Id \""+ wikidataId +"\"", e);
        }
        return wikidataElementInfos;
    }

    public void predictForPreannotation(File fileInput, File fileOutput) throws Exception {
        // get the wikiId and class from the new csv file
        TrainerGenerator trainerGenerator = new TrainerGenerator();
        List<WikidataElementInfos> inputList = new ArrayList<>();
        inputList = trainerGenerator.extractData(fileInput);
        String[] headerPredict = {"WikidataID,LabelWikidata,Class"};
        String resultPredict, label, wikidata = null;
        try {
            csvWriter = new CSVWriter(new FileWriter(fileOutput), ',', CSVWriter.NO_QUOTE_CHARACTER);
            // header's file
            csvWriter.writeNext(headerPredict);
            for (WikidataElementInfos wikiElement : inputList) {
                wikidata = wikiElement.getWikidataId();
                // get the prediction result of every wikidata Id in the csv file
                resultPredict = predict(wikidata).getPredictedClass();
                // get the label of every wikidata Id in the csv file
                FeatureDataExtractor extractor = new FeatureDataExtractor(wrapper);

                final WikidataElementInfos wikidataElement = extractor.getFeatureWikidata(wikidata);
                label = wikidataElement.getLabel();
                // to fill the null result by string "null"
                if (resultPredict == null){
                    resultPredict = "null";
                }
                // write the result into a new csv file
                String[] dataPredict = {wikiElement.getWikidataId(), label, resultPredict};
                csvWriter.writeNext(dataPredict);
            }

        } catch (RuntimeException e){
            LOGGER.info("Some errors encountered when collecting some features for predicting a Wikidata Id \""+ wikidata +"\"", e);
        }
        System.out.print("Result in " + fileOutput);
        csvWriter.flush();
        csvWriter.close();
    }

    public static void main(String[] args) throws Exception {
        String fileInput = NerdKidPaths.DATA_CSV + "/NewElements.csv";
        String fileOutput = NerdKidPaths.RESULT_CSV + "/ResultPredictedClass.csv";

        WikidataFetcherWrapper wrapper = new NerdKBFetcherWrapper();
        WikidataNERPredictor wikidataNERPredictor = new WikidataNERPredictor(wrapper);
        System.out.println("Processing the pre-annotation ...");
        wikidataNERPredictor.predictForPreannotation(new File(fileInput), new File(fileOutput));
    }
}
