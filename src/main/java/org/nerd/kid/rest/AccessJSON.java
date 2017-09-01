package org.nerd.kid.rest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AccessJSON {
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
}
