package org.nerd.kid.extractor;

import org.nerd.kid.preprocessing.CSVFIleWriter;
import org.nerd.kid.preprocessing.CSVFileReader;
import org.nerd.kid.rest.NERDResponseJSONReader;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    // collect and combine data from several files csv
    public void getWikidataIdFromFilesCsv(String fileInput1, String fileInput2) throws Exception{
        CSVFileReader readCSVFile = new CSVFileReader();
        ArrayList<String> resultElementInput1 = readCSVFile.readWikiIdCsv(fileInput1);
        ArrayList<String> resultElementInput2 = readCSVFile.readWikiIdCsv(fileInput2);
        ArrayList<String> resultElement = new ArrayList<>(resultElementInput1);
        resultElement.addAll(resultElementInput2);

        // put the distinct elements
        List<String> resultElementDistinct = resultElement.stream().distinct().collect(Collectors.toList());

        // put the result in CSV file
        CSVFIleWriter createCSVFIle = new CSVFIleWriter();
        String csvfiles = "data/preannotation/dataPreannotationCombination.csv";
        FileWriter writer = new FileWriter(csvfiles);
        try {

            // number of elements
            for (int i = 0; i < resultElementDistinct.size(); i++) {
                createCSVFIle.writeLine(writer, Arrays.asList(resultElementDistinct.get(i)));
            }
        } finally {
            writer.flush();
            writer.close();
        }
    }
}
