package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CallRestAPINERD {
    // using Curl for accessing API REST Nerd / entity-fishing
    public void useCurl(String url, String query) throws Exception {

        System.out.println("** Accessing REST API Nerd, Example in /data/example/exampleCurlNERD.txt **");

        // adding new elements of command in array list of string
        ArrayList<String> element = new ArrayList<String>();

        element.add("curl");
        element.add(url);
        //element.add("http://cloud.science-miner.com/nerd/service/disambiguate");
        element.add("-XPOST");
        element.add("-F");

        // checking the user input whether or not begin with "query="
        if (query.startsWith("query="))
            element.add(query);
        else
            element.add("query=" + query);

        //element.add("query={ \"termVector\": [ { \"term\" : \"computer science\", \"score\" : 0.3 }, { \"term\" : \"engine\", \"score\" : 0.1 } ], \"language\": { \"lang\": \"en\" }, \"resultLanguages\": [\"de\"], \"nbest\": 0, \"customisation\": \"generic\" }");
        System.out.println(element);

        // converting array list of string to string array
        String[] command = element.toArray(new String[element.size()]);

        // executing the command
        Process process = Runtime.getRuntime().exec(command);

        // getting the result of execution
        StringBuilder processOutput = new StringBuilder();
        try (BufferedReader processOutputReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));) {
            String readLine;
            while ((readLine = processOutputReader.readLine()) != null) {
                processOutput.append(readLine + System.lineSeparator());
            }
            process.waitFor();
        }

        System.out.println(processOutput);

        // formatting the result
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JSONParser jsonParser = new JSONParser();

        // formatting the result into human readable format
        Object object = jsonParser.parse(processOutput.toString());
        String prettyJsonString = gson.toJson(object);

        // writing the result into JSON file
        // for getting output stream of the file for writing the result
        File fl = new File("result/Result_CurlNERD.json");

        BufferedWriter result = new BufferedWriter(new FileWriter(fl));

        result.newLine();
        result.write(prettyJsonString);
        result.close();
    }

    // accessing JSON file containing NERD's result of annotation and ambiguation
    public Map<String, ArrayList<String>> readJSON() throws Exception {
        Map<String, ArrayList<String>> listDataJSON = new HashMap<String, ArrayList<String>>();
        ArrayList<String> dataJSONWikiId = new ArrayList<String>();
        ArrayList<String> dataJSONType = new ArrayList<String>();

        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(new FileReader("result/Result_CurlNERD.json"));
        JSONObject jsonObject = (JSONObject) object;

        JSONArray entities = (JSONArray) jsonObject.get("entities");

        for (int i = 0; i < entities.size(); i++) {
            JSONObject jsonObjectRow = (JSONObject) entities.get(i);
            if (jsonObjectRow.get("wikidataId") != null) {
                dataJSONWikiId.add(jsonObjectRow.get("wikidataId").toString());
                if (jsonObjectRow.get("type") != null) {
                    dataJSONType.add(jsonObjectRow.get("type").toString());
                } else
                    dataJSONType.add("");
            }
        }

        //putting the result into HashMap
        listDataJSON.put("WikidataId", dataJSONWikiId);
        listDataJSON.put("ClassNerd", dataJSONType);

        return listDataJSON;
    }

    public void printJSON(Map<String, ArrayList<String>> resultJSON) throws Exception {
        Map<String, ArrayList<String>> result = resultJSON;
        ArrayList<String> dataJSONWikiId = new ArrayList<String>();
        ArrayList<String> dataJSONType = new ArrayList<String>();

        for (Map.Entry<String, ArrayList<String>> entry : result.entrySet()) {
            if (entry.getKey() == "WikidataId") {
                dataJSONWikiId = entry.getValue();
            } else if (entry.getKey() == "ClassNerd") {
                dataJSONType = entry.getValue();
            }
        }
        System.out.println("Wikidata Id - Class in Nerd:");
        for (int i = 0; i < dataJSONWikiId.size(); i++) {
            System.out.println(dataJSONWikiId.get(i) + " - " + dataJSONType.get(i));
        }
    }
}
