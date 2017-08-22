package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class callRestAPINERD {


    // using Curl for accessing API REST Nerd / entity-fishing
    public void useCurl(String url, String query) throws Exception{

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
            element.add("query="+query);

        //element.add("query={ \"termVector\": [ { \"term\" : \"computer science\", \"score\" : 0.3 }, { \"term\" : \"engine\", \"score\" : 0.1 } ], \"language\": { \"lang\": \"en\" }, \"resultLanguages\": [\"de\"], \"nbest\": 0, \"customisation\": \"generic\" }");
        System.out.println(element);

        // converting array list of string to string array
        String[] command = element.toArray(new String[element.size()]);

        // executing the command
        Process process = Runtime.getRuntime().exec(command);

        // getting the result of execution
        StringBuilder processOutput = new StringBuilder();
        try (BufferedReader processOutputReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));)
        {
            String readLine;
            while ((readLine = processOutputReader.readLine()) != null)
            {
                processOutput.append(readLine + System.lineSeparator());
            }   process.waitFor();
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
    public List<String> readJSON() throws Exception{

        List<String> dataJSON = new ArrayList<String>();

        JSONParser parser = new JSONParser();
        Object object = parser.parse(new FileReader("result/Result_CurlNERD.json"));
        JSONObject jsonObject = (JSONObject) object;

        JSONArray entities = (JSONArray) jsonObject.get("entities");

        // reading all data contained in "entities"
        for (int i = 0; i<entities.size(); i++){
            JSONObject jsonObjectRow = (JSONObject) entities.get(i);
            if (jsonObjectRow.get("wikidataId") != null) {
                dataJSON.add(jsonObjectRow.get("wikidataId").toString());
            }
        }
        System.out.println("Wikidata ID from JSON File : " + dataJSON);

        // getting unique data of Wikidata ID from the entities in NERD application
        List<String> uniqueDataJSON = dataJSON.stream().distinct().collect(Collectors.toList());
        System.out.println("Unique Wikidata ID from JSON File : " + uniqueDataJSON);

        return uniqueDataJSON;
    }
}
