package org.nerd.kid.extractor.wikidata;

import org.nerd.kid.data.WikidataElement;

public interface WikidataFetcherWrapper {
    WikidataElement getElement(String wikiId) throws Exception;

}
