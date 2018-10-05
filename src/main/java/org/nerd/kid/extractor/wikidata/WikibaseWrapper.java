package org.nerd.kid.extractor.wikidata;

import org.nerd.kid.data.WikidataElement;
import org.nerd.kid.extractor.FeatureFileExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WikibaseWrapper implements WikidataFetcherWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(WikibaseWrapper.class);

    private WikibaseDataFetcher wikibaseDataFetcher = WikibaseDataFetcher.getWikidataDataFetcher();

    FeatureFileExtractor featureFileExtractor = new FeatureFileExtractor();

    public WikidataElement getElement(String wikiId) throws Exception {
        String labelItem = null;

        System.out.println("Fetch data from wikidata " + wikiId);

        WikidataElement wikidataElement = new WikidataElement();

        wikibaseDataFetcher.getEntityDocument(wikiId);

        ItemDocument document = (ItemDocument) wikibaseDataFetcher.getEntityDocument(wikiId);

        if (document != null) {

            // get the label of wikidata Id
            labelItem = document.getLabels().get("en").getText();

            // replace commas in Wikidata labels with the underscore to avoid incorrect extraction in the Csv file
            if(labelItem.contains(",")){
                labelItem = labelItem.replace(",", "_");
            }

            if (labelItem.equals(null)) {
                labelItem = "Null";

                // list for storing properties and its values
                //Map<String, List<String>> dataPropertyValue = featureFileExtractor.loadFeatures();
                Map<String, List<String>> dataPropertyValue = new HashMap<>();
                for (StatementGroup statementGroup : document.getStatementGroups()) {

                    // get the properties
                    String property = statementGroup.getProperty().getId().toString().trim();

                    // get the values
                    for (Statement statement : statementGroup) {
                        if (statement.getClaim().getMainSnak() instanceof ValueSnak) {
                            Value value = (statement.getClaim().getMainSnak()).getValue();
                            if (value instanceof ItemIdValue) {

                                // so we have property:property; value:valueofProperty
                                String valueOfProperty = ((ItemIdValue) value).getId().toString().trim();

                                //if the property has not been added in the map, create a new property-values
                                if (dataPropertyValue.get(property) == null) {
                                    List<String> values = new ArrayList<>();
                                    values.add(valueOfProperty);
                                    dataPropertyValue.put(property, values);
                                } else {
                                    //if the property exists in the map,just add the values with the same property
                                    dataPropertyValue.get(property).add(valueOfProperty);
                                }
                            }
                        }
                    }
                }

                wikidataElement.setId(wikiId);
                wikidataElement.setLabel(labelItem);
                wikidataElement.setProperties(dataPropertyValue);

            }
        } else {
            System.out.println("Data couldn't be fetched.");
        }

        return wikidataElement;
    } // end of method
} // end of class

