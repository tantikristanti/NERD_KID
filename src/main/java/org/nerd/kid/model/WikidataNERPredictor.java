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
import smile.classification.RandomForest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WikidataNERPredictor {
    private CSVWriter csvWriter = null;

    private XStream streamer = new XStream();
    private RandomForest forest = null;

    public void loadModel() {
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

        // extract the characteristices of entities directly from Wikidata
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
            String[] header = {"WikidataID,Class"};
            csvWriter.writeNext(header);
            for (WikidataElementInfos wikiElement : inputList) {
                // predict every wikidata Id in the file
                String resultPredict = predict(wikiElement.getWikidataId()).getPredictedClass();
                String[] data = {wikiElement.getWikidataId(), resultPredict};
                csvWriter.writeNext(data);
            }

        } finally {
            csvWriter.flush();
            csvWriter.close();
        }
        System.out.print("Result in 'result/csv/ResultPredictedClass.csv'");

    }
}
