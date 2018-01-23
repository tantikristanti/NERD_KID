package org.nerd.kid.model;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PredictorTest {
    WikidataNERPredictor target;

    @Before
    public void setUp() throws Exception {
        target = new WikidataNERPredictor();

    }

    @Test
    @Ignore("Not a unit test")
    public void predict() throws Exception {
        assertThat(target.predict("Q490").getPredictedClass(), is("LOCATION"));

        assertThat(target.predict("Q12345").getPredictedClass(), is("PERSON"));
        assertThat(target.predict("Q1234").getPredictedClass(), is("LOCATION"));
        assertThat(target.predict("Q55555").getPredictedClass(), is("LOCATION"));
        assertThat(target.predict("Q666").getPredictedClass(), is("LOCATION"));

        //ibm
        assertThat(target.predict("Q202712").getPredictedClass(), is("LOCATION"));

        //apple
        assertThat(target.predict("Q312").getPredictedClass(), is("LOCATION"));

        //charlemagne
        assertThat(target.predict("Q3044").getPredictedClass(), is("PERSON"));

        //11th century
        assertThat(target.predict("Q7063").getPredictedClass(), is("LOCATION"));

    }

}