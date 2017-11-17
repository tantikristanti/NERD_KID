package org.nerd.kid.model;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.nerd.kid.data.WikidataElement;
import org.nerd.kid.data.WikidataElementInfos;
import org.nerd.kid.extractor.ClassExtractor;
import org.nerd.kid.extractor.FeatureWikidataExtractor;
import org.nerd.kid.extractor.WikidataFetcherWrapper;
import smile.classification.RandomForest;

import java.io.InputStream;
import java.util.List;

public class WikidataNERPredictor {
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
        WikidataFetcherWrapper wrapper = new WikidataFetcherWrapper();
        FeatureWikidataExtractor extractor = new FeatureWikidataExtractor();
        extractor.setWikidataFetcherWrapper(wrapper);

        final WikidataElementInfos wikidataElement = extractor.getFeatureWikidata(wikidataId);
        final int length = wikidataElement.getFeatureVector().length;
        double[] rawFeatures = new double[length];
        for (int i = 0; i < length; i++) {
            rawFeatures[i] = ((double) wikidataElement.getFeatureVector()[i]);
        }
        int prediction = forest.predict(rawFeatures);

        List<String> classMapper = new ClassExtractor().loadClasses();
        wikidataElement.setPredictedClass(classMapper.get(prediction));

        return wikidataElement;
    }
}
