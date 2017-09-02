package org.nerd.kid.rest;

import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CallAPIWikidata {

    // object of Wikidata's data fetcher
    private WikibaseDataFetcher wikibaseDataFetcher = WikibaseDataFetcher.getWikidataDataFetcher();
    PredictData predictData = new PredictData();

    public String[][] appendNewTestData() throws Exception {
        //read a file
        BufferedReader reader = new BufferedReader(new FileReader("data/Training.arff"));
        String nextLine;
        List<String> listProperties = new ArrayList<String>();
        String splitBy = " ";

        // put to a new file
        //PrintStream writerCsv = new PrintStream(new FileOutputStream("data/Testing.csv"));

        // getting the data of class
        while ((nextLine = reader.readLine()) != null) {
            if (nextLine.startsWith("@ATTRIBUTE")) {
                if (!nextLine.contains("class")) {
                    String[] result = nextLine.split(splitBy);
                    listProperties.add(result[1]);
                }
            }
        }

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
        int colNumberTestX = listProperties.size();
        int colNumberNewData = listProperties.size() + 4;

        double[] testX = new double[listProperties.size()];
        String[][] matrixNewData = new String[rowNumber][colNumberNewData];

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
                break;
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

            // for each column of testing x data
            for (int j=0;j<colNumberTestX;j++){
                //print out the properties list
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
                    testX[j] = 1.0;
                else
                    testX[j] = 0.0;
            }

            // predict testX
            String resultPredict = predictData.predictNewTestData(testX);

            // for each column of matrix new data
            for (int j = 0; j < colNumberNewData; j++) {
                //print out the properties list
                //if (j < colNumberNewData - 1) { // for checking wheather is it in the end of the class or not
                    if (j == 0){ // first column : Wikidata Id
                        matrixNewData[i][j] = elementWikiId;
                    }
                    else if(j== 1){ //second column : label of Wikidata Id
                        matrixNewData[i][j] = labelWiki.get(i);
                    }
                    else if(j == 2){ // third column : Nerd's class
                        matrixNewData[i][j] = (dataJSONType.get(i) == "" ? "Null" : dataJSONType.get(i));
                    }
                    else if(j == 3){ // fourth column : predicted data
                        matrixNewData[i][j] = resultPredict;
                    }
                    else{
                        for(int k=0;k<listProperties.size();k++) {
                            String propertySearched = listProperties.get(k);
                            Boolean found = false;

                            // put the data for each cell of each row
                            for (int l = 0; l < dataPropertyWiki.size(); l++) {
                                if (propertySearched.equals(dataPropertyWiki.get(l)))
                                    found = true;
                            }

                            for (int l = 0; l < dataValuePropertyWiki.size(); l++) {
                                if (propertySearched.equals(dataValuePropertyWiki.get(l)))
                                    found = true;
                            }
                            // if property searched is found
                            if (found)
                                matrixNewData[i][j] = "1";
                            else
                                matrixNewData[i][j] = "0";
                        }

                }
            } // end of column of matrix
        } // end of row of matrix

        // header
        System.out.println("WikidataID" + ";" + "labelWikidata" + ";" + "ClassNerd" + ";" + "PredictedClass");
        //writerCsv.println("WikidataID" + ";" + "labelWikidata" + ";" + "ClassNerd" + ";" + "PredictedClass");
        for (int i = 0;i<listProperties.size();i++){
            System.out.print(listProperties.get(i));
            //writerCsv.print(listProperties.get(i));
            if (i == listProperties.size()-1){
            System.out.print(listProperties.get(i)+";");
            //writerCsv.print(listProperties.get(i)+";");
            }
        }

        // print the result of matrixNewData into Arff or Csv file
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < colNumberNewData; j++) {
                //writerCsv.print(matrixNewData[i][j]);
                if (j != colNumberNewData - 1) { // if it is not the last element
                    //writerCsv.print(";");
                }

                System.out.print(matrixNewData[i][j] + "\t");
            }
            //writerCsv.print("\n");
            System.out.print("\n");
        }
        //writerCsv.flush();
        //writerCsv.close();

        return matrixNewData;
    }


}
