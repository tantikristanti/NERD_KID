package org.nerd.kid.model;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nerd.kid.data.WikidataElement;
import org.nerd.kid.data.WikidataElementInfos;
import org.nerd.kid.extractor.wikidata.WikidataFetcherWrapper;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class WikidataNERPredictorTest {
    WikidataNERPredictor wikidataNERPredictor;
    WikidataElement wikidataElement;
    WikidataFetcherWrapper wrapper;
    String predictionResult;
    List<String> propertiesNoValue = new ArrayList<>();
    Map<String, List<String>> properties = new HashMap<>();

    @Before
    public void setUp() {
        try {
            wikidataNERPredictor = new WikidataNERPredictor(wrapper);
            wikidataElement = new WikidataElement();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error : " + e.getMessage());
        }
    }

    @Test
    @Ignore("For testing later on")
    public void predictWikidataId() {
        assertThat(wikidataNERPredictor.predict("Q1077").getPredictedClass(), is("CREATION"));
        assertThat(wikidataNERPredictor.predict("Q490").getPredictedClass(), is("LOCATION"));
        assertThat(wikidataNERPredictor.predict("Q12345").getPredictedClass(), is("PERSON"));
        assertThat(wikidataNERPredictor.predict("Q55555").getPredictedClass(), is("CREATION"));
    }

    @Test
    public void predictWikidataElement1() {
        wikidataElement.setId("Q1011"); // Cape Verde (Class: LOCATION)
        propertiesNoValue = Arrays.asList("P1566", "P30", "P36");
        properties.put("P31", Arrays.asList("Q6256"));
        wikidataElement.setProperties(properties);
        wikidataElement.setPropertiesNoValue(propertiesNoValue);
        predictionResult = wikidataNERPredictor.predict(wikidataElement).getPredictedClass();

        assertThat(predictionResult, is("LOCATION"));
    }

    @Test
    public void predictWikidataElement2() {
        wikidataElement.setId("Q103748"); // Team Stronach (Class: ORGANISATION)
        propertiesNoValue = Arrays.asList("P159");
        properties.put("P31", Arrays.asList("Q7278"));
        wikidataElement.setProperties(properties);
        wikidataElement.setPropertiesNoValue(propertiesNoValue);
        predictionResult = wikidataNERPredictor.predict(wikidataElement).getPredictedClass();

        assertThat(predictionResult, is("ORGANISATION"));
    }

    // test if a list containing properties without any values is null
    @Test
    public void predictWikidataElement3() {
        wikidataElement.setId("Q76"); // Barack Obama (Class: PERSON)
        properties.put("P21", Arrays.asList("Q6581097"));
        properties.put("P31", Arrays.asList("Q5"));
        wikidataElement.setProperties(properties);
        wikidataElement.setPropertiesNoValue(propertiesNoValue);
        predictionResult = wikidataNERPredictor.predict(wikidataElement).getPredictedClass();

        assertThat(predictionResult, is("PERSON"));

    }

    // test if a map containing properties with values is null
    @Test
    public void predictWikidataElement4() {
        wikidataElement.setId("Q1097"); // Berlin Hauptbahnhof (Class: INSTALLATION)
        propertiesNoValue = Arrays.asList("P1566","P84");
        wikidataElement.setProperties(properties);
        wikidataElement.setPropertiesNoValue(propertiesNoValue);
        predictionResult = wikidataNERPredictor.predict(wikidataElement).getPredictedClass();

        assertThat(predictionResult, is("INSTALLATION"));

    }
}