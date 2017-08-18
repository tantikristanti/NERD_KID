package restAPI;

import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

import java.util.Map;

public class callAPIWikidata {

    public static void main(String[] args) throws Exception{
        //public void accessAPI() throws Exception {


        System.out.println("** Aceess API of Wikidata **");

        // object of Wikidata's data fetcher
        WikibaseDataFetcher wikibaseDataFetcher = WikibaseDataFetcher.getWikidataDataFetcher();

        // for fetching data for entities
        EntityDocument entities = wikibaseDataFetcher.getEntityDocument("Q76");

        ItemDocument itemDocument = (ItemDocument) entities;

        if (itemDocument == null){
            System.out.println(((ItemDocument) entities).getItemId() + "couldn't be fetched");
            return;
        }

        for (StatementGroup statementGroup : itemDocument.getStatementGroups()){
            System.out.println(statementGroup.getProperty().getId());
        }

        /**for (StatementGroup statementGroup : entities.)){
            System.out.println("");
        }**/
    }
}
