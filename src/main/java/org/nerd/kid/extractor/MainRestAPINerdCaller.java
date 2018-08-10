package org.nerd.kid.extractor;

import org.nerd.kid.extractor.grobidNer.WikidataIdClassExtractor;
import org.nerd.kid.service.NerdClient;
import org.nerd.kid.service.NerdEntity;
import org.nerd.kid.service.NerdKidPaths;

import java.util.List;
import java.util.Scanner;

/*
 * This is class is meant to extract WikiIds and Classes as results from text disambiguation of Entity-Fishing Rest API
 * http://cloud.science-miner.com/nerd/service/disambiguate
 * */

public class MainRestAPINerdCaller {
    public static void main(String[] args) throws Exception {
        String pathJSON = NerdKidPaths.DATA_JSON;
        String pathCSV = NerdKidPaths.DATA_CSV;
        String fileOutputJson = pathJSON + "/Result_EntityFishingTextDisambiguation.json";
        String fileOutputCsv = pathCSV + "/NewElements.csv";
        String result = null;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Text to be disambiguated with Entity-Fishing API Rest : ");
        String text = scanner.nextLine();
        System.out.println("Language (en, fr, de, it, es) : ");
        String lang = scanner.nextLine();
        NerdClient nerdClient = new NerdClient("cloud.science-miner.com/nerd/service");
        // text disambiguation
        if (lang != null && !lang.isEmpty()) {
            result = nerdClient.textDisambiguate(text, lang);

        } else {
            // language as default in English
            result = nerdClient.textDisambiguate(text, "en");
        }

        // save the result to Json file
        String resultInJson = nerdClient.toJson(result);
        nerdClient.saveToFileJson(resultInJson, fileOutputJson);

        // extract and save to Json Csv
        WikidataIdClassExtractor wikidataIdClassExtractor = new WikidataIdClassExtractor();
        List<NerdEntity> extractionFromJson = wikidataIdClassExtractor.parseFromJsonFileToString(fileOutputJson);
        wikidataIdClassExtractor.saveToFileCsv(extractionFromJson, fileOutputCsv);

        System.out.println(result);
        System.out.println("The disambiguation result from Entity-Fishing in JSON format is in " + fileOutputJson);
        System.out.println("The disambiguation result from Entity-Fishing in CSV format is in " + fileOutputCsv);
    }
}

