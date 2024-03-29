package org.nerd.kid.model;

import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nerd.kid.data.WikidataElement;
import org.nerd.kid.extractor.wikidata.NerdKBFetcherWrapper;
import org.nerd.kid.extractor.wikidata.NerdKBLocalFetcherWrapper;
import org.nerd.kid.extractor.wikidata.WikidataFetcherWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class WikidataNERPredictorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(WikidataNERPredictorTest.class);

    WikidataNERPredictor wikidataNERPredictor1, wikidataNERPredictor2;
    WikidataElement wikidataElement;
    WikidataFetcherWrapper wrapper1, wrapper2;

    String predictionResult;
    List<String> propertiesNoValue = new ArrayList<>();
    Map<String, List<String>> properties = new HashMap<>();

    @Before
    public void setUp() {
        try {
            // statements collected from entity-fishing API Service (http://nerd.huma-num.fr/nerd/service/kb/concept)
            wrapper1 = new NerdKBFetcherWrapper();
            wikidataNERPredictor1 = new WikidataNERPredictor(wrapper1);

            // statements collected from entity-fishing API Service (http://localhost:8090/service/kb/concept/)
            wrapper2 = new NerdKBLocalFetcherWrapper();
            wikidataNERPredictor2= new WikidataNERPredictor(wrapper2);

            wikidataElement = new WikidataElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Ignore("This test needs to make sure the entity-fishing service on huma-num server is activated")
    // predict the NER of Wikdiata element based on the statements collected from entity-fishing API Service
    public void predictWikidataId1() {
        assertEquals(wikidataNERPredictor1.predict("Q1077").getPredictedClass(), is("CREATION")); //Q1077-Star Trek: The Original Series
        assertEquals(wikidataNERPredictor1.predict("Q490").getPredictedClass(), is("LOCATION")); //Q490-Milan
        assertEquals(wikidataNERPredictor1.predict("Q12345").getPredictedClass(), is("PERSON")); //Q12345-Count von Count
        assertEquals(wikidataNERPredictor1.predict("Q55555").getPredictedClass(), is("CREATION")); //Q55555-19 Part One: Boot Camp
        assertEquals(wikidataNERPredictor1.predict("Q8454").getPredictedClass(), is("OTHER")); //Q8454-capital punishment
        assertEquals(wikidataNERPredictor1.predict("Q12554").getPredictedClass(), is("EVENT")); // Q12554-Middle Ages
    }

    @Test
    @Ignore("This test needs to make sure the entity-fishing service on localhost on port 8090 is activated")
    // predict the NER of Wikdiata element based on the statements collected from entity-fishing API localhost service
    public void predictWikidataId2() {
        assertEquals(wikidataNERPredictor2.predict("Q1077").getPredictedClass(), is("CREATION")); //Q1077-Star Trek: The Original Series
        assertEquals(wikidataNERPredictor2.predict("Q490").getPredictedClass(), is("LOCATION")); //Q490-Milan
        assertEquals(wikidataNERPredictor2.predict("Q12345").getPredictedClass(), is("PERSON")); //Q12345-Count von Count
        assertEquals(wikidataNERPredictor2.predict("Q55555").getPredictedClass(), is("CREATION")); //Q55555-19 Part One: Boot Camp
        assertEquals(wikidataNERPredictor2.predict("Q8454").getPredictedClass(), is("OTHER")); //Q8454-capital punishment
        assertEquals(wikidataNERPredictor2.predict("Q12554").getPredictedClass(), is("EVENT")); // Q12554-Middle Ages
    }

    @Test
    @Ignore("This entity doesn't exist")
    public void predictWikidataIdNull() {
        String wikidataIdNull = "Q26023384";
        try {
            assertEquals(wikidataNERPredictor1.predict(wikidataIdNull).getPredictedClass(), is(IsNull.nullValue())); //Q26023384-This entity does not exist
        }catch (RuntimeException e){
            LOGGER.info("\"" + wikidataIdNull + "\" might be does not exist in Wikidata knowledge base", e);
        }
    }

    @Test
    public void predictWikidataElement1() {
        wikidataElement.setId("Q1011"); // Cape Verde (Class: LOCATION)
        propertiesNoValue = Arrays.asList("P1566", "P30", "P36");
        properties.put("P31", Arrays.asList("Q6256"));
        wikidataElement.setProperties(properties);
        wikidataElement.setPropertiesNoValue(propertiesNoValue);
        predictionResult = wikidataNERPredictor1.predict(wikidataElement).getPredictedClass();

        assertEquals(predictionResult, "LOCATION");
    }

    @Test
    @Ignore("Predicted as OTHER")
    public void predictWikidataElement2MightNotWork() {
        wikidataElement.setId("Q103748"); // Team Stronach (Class: ORGANISATION)
        propertiesNoValue = Arrays.asList("P159");
        properties.put("P31", Arrays.asList("Q7278"));
        wikidataElement.setProperties(properties);
        wikidataElement.setPropertiesNoValue(propertiesNoValue);
        predictionResult = wikidataNERPredictor1.predict(wikidataElement).getPredictedClass();

        assertEquals(predictionResult, is("ORGANISATION"));
    }

    // test if a list containing properties without any values is null
    @Test
    public void predictWikidataElement3() {
        wikidataElement.setId("Q76"); // Barack Obama (Class: PERSON)
        properties.put("P21", Arrays.asList("Q6581097"));
        properties.put("P31", Arrays.asList("Q5"));
        wikidataElement.setProperties(properties);
        wikidataElement.setPropertiesNoValue(propertiesNoValue);
        predictionResult = wikidataNERPredictor1.predict(wikidataElement).getPredictedClass();
        System.out.println(predictionResult);
        assertEquals(predictionResult, "PERSON");

    }

    // test if a map containing properties with values is null
    @Test
    @Ignore("Predicted as LOCATION")
    public void predictWikidataElement4MightNotWork() {
        wikidataElement.setId("Q1097"); // Berlin Hauptbahnhof (Class: INSTALLATION)
        propertiesNoValue = Arrays.asList("P1566","P84");
        wikidataElement.setProperties(properties);
        wikidataElement.setPropertiesNoValue(propertiesNoValue);
        predictionResult = wikidataNERPredictor1.predict(wikidataElement).getPredictedClass();

        assertEquals(predictionResult, is("INSTALLATION"));

    }
}