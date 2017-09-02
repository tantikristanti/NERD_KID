package org.nerd.kid.rest;

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

    public void appendNewTestData() throws Exception {
        WikibaseDataFetcher wikibaseDataFetcher = WikibaseDataFetcher.getWikidataDataFetcher();
        //read a file
        BufferedReader reader = new BufferedReader(new FileReader("data/Training.arff"));
        String nextLine;
        List<String> listProperties = new ArrayList<String>();
        String splitBy = " ";

        // put to a new file
        PrintStream writerArff = new PrintStream(new FileOutputStream("data/Testing.arff"));
        PrintStream writerCsv = new PrintStream(new FileOutputStream("data/Testing.csv"));

        writerArff.println("@RELATION Testing\n");

        // getting the data of class
        while ((nextLine = reader.readLine()) != null) {
            if (nextLine.startsWith("@ATTRIBUTE")) {
                writerArff.println(nextLine);
                if (!nextLine.contains("class")) {
                    String[] result = nextLine.split(splitBy);
                    listProperties.add(result[1]);
                }
            }
        }

        writerArff.println("\n@DATA");

        // stop the reader buffer
        reader.close();

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

        // number of row and column needed
        int rowNumber = dataJSONWikiId.size();
        int colNumber = listProperties.size() + 1;
        String[][] matrixNewData = new String[rowNumber][colNumber];

        List<String> labelWiki = new ArrayList<String>();

        // rows are for examples, columns are for properties and its class
        for (int i = 0; i < rowNumber; i++) {
            // getting every element of Wikidata Id from JSON file
            String elementWikiId = dataJSONWikiId.get(i);

            // getting entity document
            EntityDocument QElementWikiId = wikibaseDataFetcher.getEntityDocument(elementWikiId);
            // getting the label of wikidata element
            if (QElementWikiId instanceof ItemDocument) {
                String labelItem = ((ItemDocument) QElementWikiId).getLabels().get("en").getText();
                labelWiki.add(labelItem);
            }

            // fetching data
            ItemDocument itemDocument = (ItemDocument) wikibaseDataFetcher.getEntityDocument(elementWikiId);

            // checking if item document is null
            if (itemDocument == null) {
                System.out.println("Data couldn't be fetched.");
                return;
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
                for (int k = 0; k < keySearch.size(); k++) {
                    if (!keySearch.get(k).equals(property))
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

            // for each column
            for (int j = 0; j < colNumber; j++) {
                //print out the properties list
                if (j < colNumber - 1) { // for checking wheather is it in the end of the class or not
                    String propertySearched = listProperties.get(j);
                    Boolean found = false;

                    // put the data for each cell of each row
                    for (int k = 0; k < dataPropertyWiki.size(); k++) {
                        if (propertySearched.equals(dataPropertyWiki.get(k)))
                            found = true;
                    }

                    for (int k = 0; k < dataValuePropertyWiki.size(); k++) {
                        if (propertySearched.equals(dataValuePropertyWiki.get(k)))
                            found = true;
                    }

                    // if property searched is found
                    if (found)
                        matrixNewData[i][j] = "1";
                    else
                        matrixNewData[i][j] = "0";
                } else
                    matrixNewData[i][j] = dataJSONType.get(i); // put the name of Nerd's class at the end of row
            } // end of column of matrix
        } // end of row of matrix

        // print the result of matrixNewData into Arff or Csv file
        for (int i = 0; i < rowNumber; i++) {
            // getting every element of Wikidata Id from JSON file
            String elementWikiId = dataJSONWikiId.get(i);
            String labelWikiGot = labelWiki.get(i);

            System.out.println(elementWikiId + ";" + labelWikiGot + ";" + dataJSONType.get(i));
            writerCsv.print(elementWikiId + ";" + labelWikiGot + ";");
            for (int j = 0; j < colNumber; j++) {
                writerArff.print(matrixNewData[i][j]);
                writerCsv.print(matrixNewData[i][j]);
                if (j != colNumber - 1) { // if it is not the last element
                    writerArff.print(",");
                    writerCsv.print(";");
                }

                System.out.print(matrixNewData[i][j] + "\t");
            }
            writerArff.print("\n");
            writerCsv.print("\n");
            System.out.print("\n");
        }
        writerCsv.flush();
        writerArff.flush();
        writerCsv.close();
        writerArff.close();    }
}
