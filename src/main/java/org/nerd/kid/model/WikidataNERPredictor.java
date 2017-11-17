package org.nerd.kid.model;

import com.thoughtworks.xstream.XStream;
import org.nerd.kid.data.WikidataElementInfos;
import org.nerd.kid.extractor.ClassExtractor;
import org.nerd.kid.extractor.FeatureWikidataExtractor;
import org.nerd.kid.extractor.WikidataFetcherWrapper;
import smile.classification.RandomForest;

import java.io.InputStream;
import java.util.List;

public class Predictor {
    private XStream streamer = new XStream();
    private RandomForest forest = null;

    public void loadModel() {
        InputStream model = this.getClass().getResourceAsStream("/model.xml");
        forest = (RandomForest) streamer.fromXML(model);
    }

    public Predictor() {
        loadModel();
    }

    public String predict(String wikidataId) {
        WikidataFetcherWrapper wrapper = new WikidataFetcherWrapper();
        FeatureWikidataExtractor extractor = new FeatureWikidataExtractor();
        extractor.setWikidataFetcherWrapper(wrapper);

        final WikidataElementInfos featureWikidata = extractor.getFeatureWikidata(wikidataId);
        final int length = featureWikidata.getFeatureVector().length;
        double[] rawFeatures = new double[length];
        for(int i = 0; i < length; i++) {
            rawFeatures[i] = ((double) featureWikidata.getFeatureVector()[i]);
        }
        int prediction = forest.predict(rawFeatures);

        List<String> classMapper = new ClassExtractor().loadClasses();

        return classMapper.get(prediction);
    }
}
