package org.nerd.kid.preannotation;

import org.nerd.kid.preprocessing.CSVFIleWriter;
import org.nerd.kid.preprocessing.CSVFileReader;
import org.nerd.kid.rest.NERDResponseJSONReader;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * extract Wikidata Id from input files
 */
public class WikidataIdExtractor {
    public void addElementFromFileJson() throws Exception {
        // access elements from JSON file
        NERDResponseJSONReader accessJSON = new NERDResponseJSONReader();
        Map<String, ArrayList<String>> result = accessJSON.readJSON();
        ArrayList<String> dataWikiId = new ArrayList<String>();
        for (Map.Entry<String, ArrayList<String>> entry : result.entrySet()) {
            if (entry.getKey() == "WikidataId") {
                dataWikiId = entry.getValue();
            }
        }

        // put the result in CSV file
        CSVFIleWriter createCSVFIle = new CSVFIleWriter();
        String csvfile = "data/preannotation/dataPreannotation.csv";
        FileWriter writer = new FileWriter(csvfile);
        try {

            // number of elements
            int numberElements = dataWikiId.size();
            for (int i = 0; i < numberElements; i++) {
                createCSVFIle.writeLine(writer, Arrays.asList(dataWikiId.get(i)));
            }
        } finally {
            writer.flush();
            writer.close();
        }

    }

    public void getWikidataIdFromFileCsv() throws Exception {
        CSVFileReader readCSVFile = new CSVFileReader();
        ArrayList<String> resultElement = readCSVFile.readCsv("data/preannotation/dataPreannotation.csv");
        for (int i = 0; i < resultElement.size(); i++) {
            System.out.println(resultElement.get(i));
        }

    }
}
