package org.nerd.kid.extractor;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class FeatureDataExtractorTest {
    FeatureDataExtractor featureDataExtractor;
    List<String> propertiesNoValue = Arrays.asList("P1001","P1128","P136","P1435","P159","P1772","P276","P50","P580","P86");

    @Before
    public void setUp() throws Exception {
        featureDataExtractor = new FeatureDataExtractor();
    }

    @Test
    public void getFeatureWikidataTest1() {
        Double[] result = featureDataExtractor.getFeatureWikidata(propertiesNoValue);
        assertThat(result[0], is(1.0));
        assertThat(result[2], is(0.0));
        assertThat(result[3], is(1.0));
        assertThat(result[4], is(0.0));
        assertThat(result[5], is(0.0));
        assertThat(result[6], is(1.0));
        assertThat(result[7], is(0.0));
        assertThat(result[8], is(0.0));
        assertThat(result[9], is(1.0));
        assertThat(result[13], is(1.0));
        assertThat(result[18], is(1.0));
        assertThat(result[25], is(1.0));
        assertThat(result[31], is(1.0));
        assertThat(result[33], is(1.0));
        assertThat(result[37], is(1.0));
    }

}