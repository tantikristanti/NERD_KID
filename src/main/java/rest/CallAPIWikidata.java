package rest;

import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

import java.util.List;

public class CallAPIWikidata {
    public void getPropertyFromId() throws Exception {

        System.out.println("** Access API of Wikidata **");

        // object of callRestAPI
        CallRestAPINERD callAPIINERD = new CallRestAPINERD();

        // object of Wikidata's data fetcher
        WikibaseDataFetcher wikibaseDataFetcher = WikibaseDataFetcher.getWikidataDataFetcher();

        List<String> entityNERD = callAPIINERD.readJSON();

        System.out.println(entityNERD.size());

        for (int i=0; i < entityNERD.size(); i++){
            System.out.println(entityNERD.get(i));
            // for fetching data for entities
            EntityDocument entities = wikibaseDataFetcher.getEntityDocument(entityNERD.get(i));

            ItemDocument itemDocument = (ItemDocument) entities;

            if (itemDocument == null){
                System.out.println(((ItemDocument) entities).getItemId() + " couldn't be fetched");
                return;
            }

            for (StatementGroup statementGroup : itemDocument.getStatementGroups()){
                System.out.printf(statementGroup.getProperty().getId()+"\t");
            }

            System.out.printf("\n");
        }
    }

    public void getValueFromProperty() throws Exception{

    }

}
