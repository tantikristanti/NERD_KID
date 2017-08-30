package rest;

import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CallAPIWikidata {
    // object of Wikidata's data fetcher
    private WikibaseDataFetcher wikibaseDataFetcher = WikibaseDataFetcher.getWikidataDataFetcher();

    public void getPropertyFromId() throws Exception {

        System.out.println("** Access API of Wikidata **");

        // object of callRestAPI
        AccessJSON accessJSON = new AccessJSON();

        Map<String, ArrayList<String>> result = accessJSON.readJSON();
        ArrayList<String> dataJSONWikiId = new ArrayList<String>();
        ArrayList<String> dataJSONType = new ArrayList<String>();

        for (Map.Entry<String, ArrayList<String>> entry : result.entrySet()) {
            if (entry.getKey() == "WikidataId") {
                dataJSONWikiId = entry.getValue();
            } else if (entry.getKey() == "ClassNerd") {
                dataJSONType = entry.getValue();
            }
        }
        System.out.println("Wikidata Id - Class in Nerd:");
        for (int i = 0; i < dataJSONWikiId.size(); i++) {
            System.out.println("\n" + dataJSONWikiId.get(i) + " - " + dataJSONType.get(i));

            // for fetching data for entities
            ItemDocument itemDocument = (ItemDocument) wikibaseDataFetcher.getEntityDocument(dataJSONWikiId.get(i));

            if (itemDocument == null) {
                System.out.println("Data couldn't be fetched.");
                return;
            }

            // only get the value of P31 (instance of) and P21 (sex or gender)
            List keySearch = new ArrayList();
            keySearch.add("P31");
            keySearch.add("P21");

            System.out.println("Properties:\t");
            for (StatementGroup statementGroup : itemDocument.getStatementGroups()) {
                System.out.print(statementGroup.getProperty().getId() + "\t");
                // getting the value of property P31 and P21
                for (int j = 0; j < keySearch.size(); j++) {
                    if (!keySearch.get(j).equals(statementGroup.getProperty().getId()))
                        continue;
                    System.out.print("\nValues for " + statementGroup.getProperty().getId() + " :\t");
                    for (Statement statement : statementGroup) {
                        if (statement.getClaim().getMainSnak() instanceof ValueSnak) {
                            Value value = ((ValueSnak) statement.getClaim().getMainSnak()).getValue();
                            if (value instanceof ItemIdValue) {
                                System.out.print(((ItemIdValue) value).getId() + "\t");
                            }
                        }
                    }
                    System.out.print("\n");
                }
            }
            System.out.printf("\n");
        }
    }
}
