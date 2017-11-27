package org.nerd.kid.model;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class PredictorTest {
    WikidataNERPredictor target;
    @Before
    public void setUp() throws Exception {
        target = new WikidataNERPredictor();

    }

    @Test
    @Ignore("Not a unit test")
    public void predict() throws Exception {
        System.out.println(target.predict("Q490").getPredictedClass());

        System.out.println(target.predict("Q12345").getPredictedClass());
        System.out.println(target.predict("Q1234").getPredictedClass());
        System.out.println(target.predict("Q55555").getPredictedClass());
        System.out.println(target.predict("Q666").getPredictedClass());

        //ibm
        System.out.println(target.predict("Q202712").getPredictedClass());

        //apple
        System.out.println(target.predict("Q312").getPredictedClass());

        //charlemagne
        System.out.println(target.predict("Q3044").getPredictedClass());

        //11th century
        System.out.println(target.predict("Q7063").getPredictedClass());

    }

}