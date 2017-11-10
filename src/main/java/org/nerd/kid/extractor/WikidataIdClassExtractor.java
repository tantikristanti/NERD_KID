package org.nerd.kid.extractor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.nerd.kid.exception.NerdKidException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * extract Wikidata Id and class from CSV file
 */

public class WikidataIdClassExtractor {

    public Map<String, String> loadIdClass() {
        try {
            return loadIdClass(new FileInputStream("data/csv/NewElements.csv"));
        } catch (FileNotFoundException e) {
            throw new NerdKidException("An exception occured while NerdKid is running.", e);
        }
    }

    public Map<String, String> loadIdClass(InputStream inputStreamWikiIdFile) {
        try {
            Map<String, String> wikiIdClassMap = new HashMap<>();
            Reader WikiIdClassMapper = new InputStreamReader(inputStreamWikiIdFile);
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(WikiIdClassMapper);

            for (CSVRecord record : records) {
                String wikiId = record.get("WikidataID");
                String claz = record.get("Class");
                wikiIdClassMap.put(wikiId, claz);
            }
            return wikiIdClassMap;
        } catch (FileNotFoundException e) {
            throw new NerdKidException("An exception occured while NerdKid is running.", e);
        } catch (IOException e) {
            throw new NerdKidException("An exception occured while NerdKid is running.", e);
        }
    }

    public void printIdclass(Map<String, String> wikiIdClassMap) throws Exception {
        for (Map.Entry<String, String> entry : wikiIdClassMap.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
    }

}
