package org.nerd.kid.extractor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * extract Wikidata Id and class from CSV file
 */

public class WikidataIdClassExtractor {

    public Map<String, String> loadIdClass() throws Exception {
        return loadIdClass(new FileInputStream("data/csv/NewElements.csv"));
    }

    public Map<String, String> loadIdClass(InputStream inputStreamWikiIdFile) throws IOException {
        Map<String, String> wikiIdClassMap = new HashMap<>();
        Reader WikiIdClassMapper = new InputStreamReader(inputStreamWikiIdFile);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(WikiIdClassMapper);

        for (CSVRecord record : records) {
            String wikiId = record.get("WikidataID");
            String claz = record.get("Class");
            wikiIdClassMap.put(wikiId, claz);
        }
        return wikiIdClassMap;
    }

    public void printIdclass(Map<String, String> wikiIdClassMap) throws Exception{
        for (Map.Entry<String, String> entry : wikiIdClassMap.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
    }

}
