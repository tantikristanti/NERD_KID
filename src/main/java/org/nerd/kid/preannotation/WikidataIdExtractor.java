package org.nerd.kid.preannotation;

import org.nerd.kid.preprocessing.CreateCSVFIle;
import org.nerd.kid.preprocessing.ReadCSVFile;
import org.nerd.kid.rest.AccessJSON;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * extract Wikidata Id from input files
 */
public class WikidataIdExtractor {
    public void addElementFromFileJson() throws Exception{
        // access elements from JSON file
        AccessJSON accessJSON = new AccessJSON();
        Map<String, ArrayList<String>> result = accessJSON.readJSON();
        ArrayList<String> dataWikiId = new ArrayList<String>();
        for(Map.Entry<String, ArrayList<String>> entry : result.entrySet()){
            if (entry.getKey() == "WikidataId"){
                dataWikiId = entry.getValue();
            }
        }

        // put the result in CSV file
        CreateCSVFIle createCSVFIle = new CreateCSVFIle();
        String csvfile = "data/preannotation/dataPreannotation.csv";
        FileWriter writer = new FileWriter(csvfile);

        // number of elements
        int numberElements = dataWikiId.size();
        for (int i=0; i<numberElements;i++){
            createCSVFIle.writeLine(writer, Arrays.asList(dataWikiId.get(i)));
        }

        writer.flush();
        writer.close();
    }

    public void getWikidataIdFromFileCsv() throws Exception{
        ReadCSVFile readCSVFile = new ReadCSVFile();
        ArrayList<String> resultElement = readCSVFile.readCsv("data/preannotation/dataPreannotation.csv");
        for (int i = 0; i< resultElement.size();i++){
            System.out.println(resultElement.get(i));
        }

    }
}
