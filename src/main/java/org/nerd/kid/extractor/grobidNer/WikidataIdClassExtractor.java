package org.nerd.kid.extractor.grobidNer;

import au.com.bytecode.opencsv.CSVWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.nerd.kid.service.NerdEntity;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WikidataIdClassExtractor {
    public List<NerdEntity> parseFromJsonFileToString(String inputFile) {
        List<NerdEntity> nerdEntityList = new ArrayList<NerdEntity>();
        WikidataIdClassExtractor wikidataIdClassExtractor = new WikidataIdClassExtractor();
        JSONParser jsonParser = new JSONParser();
        try {

            Object object = jsonParser.parse(new FileReader(inputFile));
            JSONObject jsonObject = (JSONObject) object;
            nerdEntityList = wikidataIdClassExtractor.parseFromJsonString(jsonObject.toString());

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (ParseException e){
            e.printStackTrace();
        }
        return nerdEntityList;
    }

    public List<NerdEntity> parseFromJsonString(String input){
        List<NerdEntity> nerdEntityList = new ArrayList<NerdEntity>();
        JSONParser jsonParser = new JSONParser();
        try {
            Object object = jsonParser.parse(input);
            JSONObject jsonObject = (JSONObject) object;

            JSONArray entities = (JSONArray) jsonObject.get("entities");
            if (entities != null) {
                for (int i = 0; i < entities.size(); i++) {
                    JSONObject jsonObjectRow = (JSONObject) entities.get(i);
                    NerdEntity nerdEntity = new NerdEntity();
                    // get rawName
                    if (jsonObjectRow.get("rawName") != null) {
                        nerdEntity.setRawName(jsonObjectRow.get("rawName").toString());
                    } else {
                        nerdEntity.setRawName("");
                    }

                    // get typeNEEntityFishing
                    if (jsonObjectRow.get("type") != null) {
                        nerdEntity.setTypeNEEntityFishing(jsonObjectRow.get("type").toString());
                    } else {
                        nerdEntity.setTypeNEEntityFishing("");
                    }

                    // get offsetStart
                    if (jsonObjectRow.get("offsetStart") != null) {
                        nerdEntity.setOffsetStart(Integer.parseInt(jsonObjectRow.get("offsetStart").toString()));
                    } else {
                        nerdEntity.setOffsetStart(-1);
                    }

                    // get offsetEnd
                    if (jsonObjectRow.get("offsetEnd") != null) {
                        nerdEntity.setOffsetEnd(Integer.parseInt(jsonObjectRow.get("offsetEnd").toString()));
                    } else {
                        nerdEntity.setOffsetEnd(-1);
                    }

                    // get nerd_score
                    if (jsonObjectRow.get("nerd_score") != null) {
                        nerdEntity.setNerdScore(Double.parseDouble(jsonObjectRow.get("nerd_score").toString()));
                    } else {
                        nerdEntity.setNerdScore(0.0);
                    }

                    // get nerd_selection_score
                    if (jsonObjectRow.get("nerd_selection_score") != null) {
                        nerdEntity.setSelectionScore(Double.parseDouble(jsonObjectRow.get("nerd_selection_score").toString()));
                    } else {
                        nerdEntity.setSelectionScore(0.0);
                    }

                    // get wikipediaExternalRef
                    if (jsonObjectRow.get("wikipediaExternalRef") != null) {
                        nerdEntity.setWikipediaExternalRef(Integer.parseInt(jsonObjectRow.get("wikipediaExternalRef").toString()));
                    } else {
                        nerdEntity.setWikipediaExternalRef(-1);
                    }

                    // get wikidataId
                    if (jsonObjectRow.get("wikidataId") != null) {
                        nerdEntity.setWikidataId(jsonObjectRow.get("wikidataId").toString());
                    } else {
                        nerdEntity.setWikidataId(null);
                    }

                    //put the result into List
                    nerdEntityList.add(nerdEntity);
                }
            }
            //return nerdEntityList;
        }catch (ParseException e){
            e.printStackTrace();
        }
        return nerdEntityList;
    }

    public void saveToFileCsv(List<NerdEntity> resultToSave, String outputFile) {
        // get only the list of Wikidata Ids and Classes from the Json result
        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(outputFile), ',', CSVWriter.NO_QUOTE_CHARACTER);
            String[] header = {"WikidataID,Class"};
            csvWriter.writeNext(header);
            // get the list of Wikidata Ids and Class
            for (NerdEntity entity : resultToSave) {
                if (entity.getWikidataId() != null) {
                    String[] data = {entity.getWikidataId(), entity.getTypeNEEntityFishing()};
                    csvWriter.writeNext(data);
                }
            }
            csvWriter.flush();
            csvWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
