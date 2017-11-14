package org.nerd.kid.extractor;

import org.nerd.kid.data.WikidataElement;
import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WikidataFetcherWrapper {

    private WikibaseDataFetcher wikibaseDataFetcher = WikibaseDataFetcher.getWikidataDataFetcher();

    FeatureFileExtractor featureFileExtractor = new FeatureFileExtractor();

    public WikidataElement getElement(String wikiId) throws Exception {

        WikidataElement wikidataElement = new WikidataElement();

        wikibaseDataFetcher.getEntityDocument(wikiId);

        ItemDocument document = (ItemDocument) wikibaseDataFetcher.getEntityDocument(wikiId);

        if (document == null) {
            System.out.println("Data couldn't be fetched.");
        }

        // get the label of wikidata Id
        String labelItem = document.getLabels().get("en").getText();
        if (labelItem.equals(null)){
            labelItem = "Null";
        }

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
        return wikidataElement;
    }
}
