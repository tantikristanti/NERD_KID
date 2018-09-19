package org.nerd.kid.model;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nerd.kid.data.WikidataElement;
import org.nerd.kid.data.WikidataElementInfos;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class WikidataNERPredictorTest {
    WikidataNERPredictor wikidataNERPredictor;
    WikidataElement wikidataElement;
    String predictionResult;
    List<String> propertiesNoValue = new ArrayList<>();
    Map<String, List<String>> properties = new HashMap<>();

    @Before
    public void setUp() {
        try {
            wikidataNERPredictor = new WikidataNERPredictor();
            wikidataElement = new WikidataElement();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void predictWikidataId() {
        assertThat(wikidataNERPredictor.predict("Q1077").getPredictedClass(), is("CREATION"));
        assertThat(wikidataNERPredictor.predict("Q490").getPredictedClass(), is("LOCATION"));
        assertThat(wikidataNERPredictor.predict("Q12345").getPredictedClass(), is("PERSON"));
        assertThat(wikidataNERPredictor.predict("Q55555").getPredictedClass(), is("CREATION"));
    }

    @Test
    public void predictWikidataElement() {
        wikidataElement.setId("Q1011"); // Cape Verde (Class: LOCATION)
        propertiesNoValue = Arrays.asList("P1566","P30","P36");
        properties.put("P31",Arrays.asList("Q6256"));
        wikidataElement.setProperties(properties);
        wikidataElement.setPropertiesNoValue(propertiesNoValue);
        predictionResult = wikidataNERPredictor.predict(wikidataElement).getPredictedClass();

        assertThat(predictionResult,is("LOCATION"));
    }
}