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
        System.out.println(target.predict("Q490"));

        System.out.println(target.predict("Q12345"));
        System.out.println(target.predict("Q1234"));
        System.out.println(target.predict("Q55555"));
        System.out.println(target.predict("Q666"));

        //ibm
        System.out.println(target.predict("Q202712"));

        //apple
        System.out.println(target.predict("Q312"));

        //charlemagne
        System.out.println(target.predict("Q3044"));

        //11th century
        System.out.println(target.predict("Q7063"));

    }

}