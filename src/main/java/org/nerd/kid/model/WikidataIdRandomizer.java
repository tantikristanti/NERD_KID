package org.nerd.kid.model;

import au.com.bytecode.opencsv.CSVWriter;
import org.nerd.kid.extractor.wikidata.NerdKBFetcherWrapper;
import org.nerd.kid.extractor.wikidata.WikidataFetcherWrapper;
import org.nerd.kid.service.NerdKidPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class WikidataIdRandomizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WikidataIdRandomizer.class);

    public List<Integer> randomItem() {
        Random random = new Random();
        List<Integer> intSet = new ArrayList<>();
        while (intSet.size() < 100) {
            intSet.add(random.nextInt(35030909) + 1); //currently the number of Wikidata Id items is 35030909
        }
        return intSet;
    }

    public void writeToCsv(List<Integer> listInput) throws IOException {
        String fileOutput = NerdKidPaths.DATA_CSV + "/NewElements.csv";
        CSVWriter csvWriter = null;
        List<String> wikiIds = new ArrayList<String>();
        String[] header = {"WikidataID,Class"};
        try {
            csvWriter = new CSVWriter(new FileWriter(fileOutput), '\n', CSVWriter.NO_QUOTE_CHARACTER);
            csvWriter.writeNext(header);
            if (listInput != null) {

                for (int val : listInput) {
                    String id = "Q" + String.valueOf(val) + ",";
                    wikiIds.add(id);
                    System.out.println(id);
                }
                csvWriter.writeNext(wikiIds.toArray(new String[wikiIds.size()]));
            }
        } catch (Exception e) {
            LOGGER.info("Some errors encountered when saving the result into a Csv file in \"" + fileOutput + "\"", e);
        } finally {
            csvWriter.flush();
            csvWriter.close();
        }
    }

    public static void main(String[] args) throws Exception {
        WikidataIdRandomizer wikidataIdRandomizer = new WikidataIdRandomizer();
        List<Integer> randomizedWikidataId = wikidataIdRandomizer.randomItem();
        wikidataIdRandomizer.writeToCsv(randomizedWikidataId);
    }
}
