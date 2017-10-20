package org.nerd.kid.preannotation;

public class MainPreAnnotation {
    public static void main(String[] args) throws Exception{
        System.out.println("Collect the elements in CSV file (data/preannotation/dataPreannotation.csv)");
        WikidataIdExtractor getDataElement = new WikidataIdExtractor();
        //getDataElement.addElementFromFileJson();
        getDataElement.getWikidataFeatureFromFileCsv();
    }
}
