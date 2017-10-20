package org.nerd.kid.preannotation;

import org.nerd.kid.arff.ArffParser;
import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FeatureWikidataExtractor {
    // object of Wikidata's data fetcher
    private WikibaseDataFetcher wikibaseDataFetcher = WikibaseDataFetcher.getWikidataDataFetcher();

    public void getFeatureWikidata(String elementWikiId) throws Exception{
        // getting entity document
        EntityDocument QElementWikiId = wikibaseDataFetcher.getEntityDocument(elementWikiId);

        // getting the label of wikidata element
        if (QElementWikiId instanceof ItemDocument) {
            String labelItem = ((ItemDocument) QElementWikiId).getLabels().get("en").getText();
        }

        // fetching data
        ItemDocument itemDocument = (ItemDocument) wikibaseDataFetcher.getEntityDocument(elementWikiId);

        // checking if item document is null
        if (itemDocument == null) {
            System.out.println("Data couldn't be fetched.");
        }

        // list for storing properties data and its values
        List<String> dataPropertyWiki = new ArrayList<String>();
        List<String> dataValuePropertyWiki = new ArrayList<String>();

        // only get the value of P31 (instance of) and P21 (sex or gender)
        List keySearch = new ArrayList();
        keySearch.add("P31");
        keySearch.add("P21");

        // getting the properties from Wikidata
        for (StatementGroup statementGroup : itemDocument.getStatementGroups()) {
            String property = statementGroup.getProperty().getId().toString().trim();
            dataPropertyWiki.add(property);

            // getting the value of property P31 and P21
            for (int i = 0; i< keySearch.size(); i++) {
                if (!keySearch.get(i).equals(property))
                    continue;
                for (Statement statement : statementGroup) {
                    if (statement.getClaim().getMainSnak() instanceof ValueSnak) {
                        Value value = ((ValueSnak) statement.getClaim().getMainSnak()).getValue();
                        if (value instanceof ItemIdValue) {
                            String valueOfProperty = ((ItemIdValue) value).getId().toString().trim();
                            String combinationValueProperty = property + "_" + valueOfProperty;
                            dataValuePropertyWiki.add(combinationValueProperty);
                        }
                    }
                }
            }
        }

        // getting the properties from training file
        ArffParser accessArff = new ArffParser();
        List<String> listProperties = accessArff.readPropertiesTrainingFile(new File("data/Training.arff"));

        // number of row and column needed
        int rowNumber = 1;
        int colNumberTestX = listProperties.size();
        int colNumberNewData = listProperties.size() + 4;
        double[][] testX = new double[rowNumber][colNumberTestX];
        String[][] matrixNewData = new String[rowNumber][colNumberNewData];

        // for the Wikidata label
        List<String> labelWiki = new ArrayList<String>();



    }
}
