package org.nerd.kid.rest;

public class MainCallAPIWikidata {
    public static void main(String[] args) throws Exception {
        CallAPIWikidata callWiki = new CallAPIWikidata();
        CreateCSVPredictedResult createCSVPredictedResult = new CreateCSVPredictedResult();
        String[][] matrixResultPredict = callWiki.appendNewTestData();

        createCSVPredictedResult.CreateNewCsvFile(matrixResultPredict);
    }
}