package org.nerd.kid.extractor;

import au.com.bytecode.opencsv.CSVWriter;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.nerd.kid.rest.NERDResponseJSONReader;
import org.nerd.kid.rest.RestAPINERDCaller;
import org.nerd.kid.service.NerdKidPaths;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/*
* Extract WikiIds and Classes directly from Nerd's services
* http://cloud.science-miner.com/nerd/service/disambiguate
* */

public class MainRestAPINerdCaller {
    public static void main(String[] args) throws Exception {
        String pathJSON = NerdKidPaths.DATA_JSON;
        String pathCSV = NerdKidPaths.DATA_CSV;
        String fileOutputJson = "Result_NERDDataExtractor.json";
        String fileOutputCsv = "NewElements.csv";
        CSVWriter csvWriter = null;
        Scanner scanner = new Scanner(System.in);

//        OptionParser optionParser = new OptionParser();
//        optionParser.accepts("output").withRequiredArg()
//                .describedAs("Directory where the list of Ids and Classes will be saved").ofType(String.class);
//
//        OptionSet parsedOptions = optionParser.parse(args);

//        if (parsedOptions.has("output")) {
//            String jsonDataPath = (String) parsedOptions.valueOf("output") + "data/json/Result_NERDDataExtractor.json";
//            String csvDataPath = (String) parsedOptions.valueOf("output") + "data/csv/NewElementsOK.csv";

            RestAPINERDCaller callAPIINERD = new RestAPINERDCaller();

            String url = "http://cloud.science-miner.com/nerd/service/disambiguate";

            System.out.println("Query (copy this format and change the part of text) : \n");
            System.out.println("{\"text\": \"[change this part]\", \"shortText\": \"\", \"termVector\": [], \"language\": { \"lang\": \"en\" }, \"entities\": [], \"onlyNER\": false, \"resultLanguages\": [ \"de\", \"fr\" ], \"nbest\": false, \"sentence\": false,    \"customisation\": \"generic\"}");
            System.out.println("\n");
            String query = scanner.nextLine();

            callAPIINERD.useCurl(url, query, pathJSON+"/"+fileOutputJson);

            NERDResponseJSONReader nerdResponseJSONReader = new NERDResponseJSONReader();
            Map<String, ArrayList<String>> resultJsonReader = nerdResponseJSONReader.readJSON(pathJSON+"/"+fileOutputJson);
            List<String> dataJSONWikiId = new ArrayList<String>();
            List<String> dataJSONClass = new ArrayList<String>();

            System.out.print("JSON Result in " + pathJSON+"/"+fileOutputJson);

            // get the list of Wikidata Ids and Class from file JSON
            for (Map.Entry<String, ArrayList<String>> entry : resultJsonReader.entrySet()) {
                if (entry.getKey() == "WikidataId") {
                    dataJSONWikiId = entry.getValue();
                } else if (entry.getKey() == "ClassNerd") {
                    dataJSONClass = entry.getValue();
                }
            }
            List<String[]> records = new ArrayList<String[]>();

            try {

                csvWriter = new CSVWriter(new FileWriter(pathCSV+"/"+fileOutputCsv), ',', CSVWriter.NO_QUOTE_CHARACTER);
                // header's file
                String[] header = {"WikidataID,Class"};
                csvWriter.writeNext(header);

                // the content of the file csv
                for (int i = 0; i < dataJSONWikiId.size(); i++) {
                    String[] data = {dataJSONWikiId.get(i), dataJSONClass.get(i)};
                    csvWriter.writeNext(data);
                }
            } finally {
                csvWriter.flush();
                csvWriter.close();
            }

            System.out.print("CSV Result in " + pathCSV + "/" + fileOutputCsv);

//        } else {
//            System.out.println("Missing parameter");
//            optionParser.printHelpOn(System.out);
//        }
    }
}

