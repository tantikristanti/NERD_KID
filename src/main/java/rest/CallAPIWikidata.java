package rest;

import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
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
            // getting every element of Wikidata Id from JSON file
            String elementWikiId = dataJSONWikiId.get(i);

            // for fetching data for entities
            ItemDocument itemDocument = (ItemDocument) wikibaseDataFetcher.getEntityDocument(dataJSONWikiId.get(i));

            //getting entity document
            EntityDocument QElementWikiId = wikibaseDataFetcher.getEntityDocument(elementWikiId);

            if (itemDocument == null) {
                System.out.println("Data couldn't be fetched.");
                return;
            }
            if (QElementWikiId instanceof ItemDocument) {
                System.out.println("\n" + elementWikiId + "(" + ((ItemDocument) QElementWikiId).getLabels().get("en").getText() + ")" + " - " + dataJSONType.get(i));
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

    public void createHeaderTestingArff() throws Exception {
        String nextLine;

        //read a file
        BufferedReader reader = new BufferedReader(new FileReader("data/Training.arff"));

        // put to a new file
        PrintStream writer = new PrintStream(new FileOutputStream("data/Testing.arff"));
        writer.println("@RELATION Testing\n");
        // getting the data of class
        while ((nextLine = reader.readLine()) != null) {
            if (nextLine.startsWith("@ATTRIBUTE")) {
                writer.println(nextLine);
            }
        }
        writer.println("\n@DATA");
        reader.close();
        writer.flush();
        writer.close();
    }

    public void appendNewDataTestArff() throws Exception {
        String nextLineHeader, nextLineProperties;

        //read a file
        BufferedReader readerHeader = new BufferedReader(new FileReader("data/Training.arff"));
        BufferedReader readerProperties = new BufferedReader(new FileReader("data/Training.arff"));

        // put to a new file
        PrintStream writerArff = new PrintStream(new FileOutputStream("data/Testing.arff"));
        PrintStream writerCsv = new PrintStream(new FileOutputStream("data/TestingTemp.csv"));

        writerArff.println("@RELATION Testing\n");
        // getting the data of class
        while ((nextLineHeader = readerHeader.readLine()) != null) {
            if (nextLineHeader.startsWith("@ATTRIBUTE")) {
                writerArff.println(nextLineHeader);
            }
        }
        writerArff.println("\n@DATA");

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

        // header for csv Testing Temp file
        writerCsv.println("WikidataId;Label;ClassNerd;");

        for (int i = 0; i < dataJSONWikiId.size(); i++) {
            // getting every element of Wikidata Id from JSON file
            String elementWikiId = dataJSONWikiId.get(i);

            // for fetching data for entities
            ItemDocument itemDocument = (ItemDocument) wikibaseDataFetcher.getEntityDocument(dataJSONWikiId.get(i));

            //getting entity document
            EntityDocument QElementWikiId = wikibaseDataFetcher.getEntityDocument(elementWikiId);

            if (itemDocument == null) {
                System.out.println("Data couldn't be fetched.");
                return;
            }
            if (QElementWikiId instanceof ItemDocument) {
                writerCsv.println(elementWikiId + ";" + ((ItemDocument) QElementWikiId).getLabels().get("en").getText() + ";" + dataJSONType.get(i));
            }

            // only get the value of P31 (instance of) and P21 (sex or gender)
            List keySearch = new ArrayList();
            keySearch.add("P31");
            keySearch.add("P21");

            while ((nextLineProperties = readerProperties.readLine()) != null) {
                for (StatementGroup statementGroup : itemDocument.getStatementGroups()) {
                    String property = statementGroup.getProperty().getId().toString().trim();
                    System.out.print(statementGroup.getProperty().getId() + "\t");
                    // getting the value of property P31 and P21
                    for (int j = 0; j < keySearch.size(); j++) {
                        if (!keySearch.get(j).equals(property))
                            continue;
                        System.out.print("\nValues for " + statementGroup.getProperty().getId() + " :\t");
                        for (Statement statement : statementGroup) {
                            if (statement.getClaim().getMainSnak() instanceof ValueSnak) {
                                Value value = ((ValueSnak) statement.getClaim().getMainSnak()).getValue();
                                if (value instanceof ItemIdValue) {
                                    System.out.print(((ItemIdValue) value).getId() + "\t");
                                    String valueProperty = ((ItemIdValue) value).getId().toString().trim();
                                    if (nextLineProperties.contains(property + "_" + valueProperty)) {
                                        writerArff.print("1,");
                                    }
                                }else if (nextLineProperties.contains(property)){
                                    writerArff.print("1,");
                                }
                                else
                                    writerArff.print("0,");
                            }
                        }
                    }
                }
            }
            writerArff.print(dataJSONType.get(i) +"\n");
        }

        readerHeader.close();
        readerProperties.close();
        writerArff.flush();
        writerArff.close();
    }
}
