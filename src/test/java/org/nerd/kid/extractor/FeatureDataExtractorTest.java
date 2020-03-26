package org.nerd.kid.extractor;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FeatureDataExtractorTest {
    FeatureDataExtractor featureDataExtractor;
    // features for item "Q1077 - Star Trek: The Original Series" with the class "CREATION"
    List<String> propertiesNoValueList = Arrays.asList("P136","P1476","P170","P179","P580","P582","P86");

    // features for item "Q1017 - Aachen" with the class "LOCATION"
    List<String> propertiesList = Arrays.asList("Q1187811","Q1549591","Q22865","Q515");
    Map<String, List<String>> propertiesMap = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        featureDataExtractor = new FeatureDataExtractor();
    }

    @Test
    public void getFeatureWikidataTestPropertiesNoValues1() {
        Double[] result = featureDataExtractor.getFeatureWikidata(propertiesNoValueList);
        // index is the row position in file resources\feature_mapper_no_value.csc (minus) 2
        assertThat(result[0], is(0.0));
        assertThat(result[1], is(0.0));
        assertThat(result[2], is(0.0));
        assertThat(result[3], is(0.0));
        assertThat(result[4], is(0.0));
        assertThat(result[5], is(0.0));
        assertThat(result[6], is(0.0));
        assertThat(result[7], is(0.0));
        assertThat(result[8], is(0.0));
        assertThat(result[9], is(0.0));
        assertThat(result[10], is(1.0));
        assertThat(result[11], is(0.0));
        assertThat(result[12], is(0.0));
        assertThat(result[13], is(0.0));
        assertThat(result[14], is(1.0));
        assertThat(result[15], is(0.0));
        assertThat(result[16], is(0.0));
        assertThat(result[19], is(0.0));
        assertThat(result[24], is(0.0));
        assertThat(result[45], is(0.0));
        assertThat(result[46], is(0.0));
        assertThat(result[50], is(1.0));
    }

    @Test
    public void getFeatureWikidataTestPropertiesNoValues2() {
        propertiesNoValueList = Arrays.asList("P1566","P30","P36");
        Double[] result = featureDataExtractor.getFeatureWikidata(propertiesNoValueList);
        // index is the row position in file resources\feature_maspper_no_value.csc (minus) 2
        assertThat(result[16], is(1.0));
        assertThat(result[39], is(1.0));
        assertThat(result[41], is(1.0));
    }

    @Test
    public void getFeatureWikidataTestProperties() {
        propertiesMap.put("P31", propertiesList);
        Double[] result = featureDataExtractor.getFeatureWikidata(propertiesMap);

        assertThat(propertiesMap.get("P31").get(0), is("Q1187811"));
        assertThat(propertiesMap.get("P31").get(1), is("Q1549591"));
        assertThat(propertiesMap.get("P31").get(2), is("Q22865"));
        assertThat(propertiesMap.get("P31").get(3), is("Q515"));

        // index is the row position in file resources\feature_mapper_no_value.csc (minus) 2
        assertThat(result[0], is(0.0));
        assertThat(result[1], is(0.0));
        assertThat(result[2], is(0.0));
        assertThat(result[3], is(0.0));
        assertThat(result[4], is(0.0));
        assertThat(result[5], is(0.0));
        assertThat(result[6], is(0.0));
        assertThat(result[7], is(0.0));
        assertThat(result[8], is(0.0));
        assertThat(result[9], is(0.0));
        assertThat(result[51], is(1.0));
        assertThat(result[72], is(1.0));
        assertThat(result[107], is(1.0));
        assertThat(result[163], is(1.0));
    }
}