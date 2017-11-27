package org.nerd.kid.extractor.wikidata;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.nerd.kid.data.WikidataElement;

import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class NerdKBFetcherWrapperTest {

    NerdKBFetcherWrapper target;

    @Before
    public void setUp() throws Exception {
        target = new NerdKBFetcherWrapper();
    }

    @Test
    public void testFromJson() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/Q1234.json");

        WikidataElement element = target.fromJson(IOUtils.toString(is, UTF_8));

        assertThat(element.getLabel(), is("Seaborgium"));
        assertThat(element.getProperties().keySet(), hasSize(9));
        assertThat(element.getProperties().get("P31"), is("Q11344"));
    }

}