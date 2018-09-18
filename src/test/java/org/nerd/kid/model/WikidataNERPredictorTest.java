package org.nerd.kid.model;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class WikidataNERPredictorTest {
    WikidataNERPredictor wikidataNERPredictor;

    @Before
    public void setUp() {
        try {
            wikidataNERPredictor = new WikidataNERPredictor();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void predictWikidataId() {
        System.out.println(wikidataNERPredictor.predict("Q490").getPredictedClass());
        assertThat(wikidataNERPredictor.predict("Q1077").getPredictedClass(), is("CREATION"));
        assertThat(wikidataNERPredictor.predict("Q490").getPredictedClass(), is("LOCATION"));
        assertThat(wikidataNERPredictor.predict("Q12345").getPredictedClass(), is("PERSON"));
        assertThat(wikidataNERPredictor.predict("Q55555").getPredictedClass(), is("CREATION"));
    }

    @Test
    public void predict1() {
    }
}