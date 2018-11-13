package org.nerd.kid.extractor.grobidNer;

import au.com.bytecode.opencsv.CSVWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.nerd.kid.service.NerdEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*A class to extract the Wikidata Ids from Grobid-Ner extraction results which are in Json format into a Csv file*/

public class WikidataIdClassExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(WikidataIdClassExtractor.class);

    public List<NerdEntity> parseFromJsonFileToString(String inputFile) {
        List<NerdEntity> nerdEntityList = new ArrayList<NerdEntity>();
        WikidataIdClassExtractor wikidataIdClassExtractor = new WikidataIdClassExtractor();
        JSONParser jsonParser = new JSONParser();
        try {

            Object object = jsonParser.parse(new FileReader(inputFile));
            JSONObject jsonObject = (JSONObject) object;
            nerdEntityList = wikidataIdClassExtractor.parseFromJsonString(jsonObject.toString());

        }catch (FileNotFoundException e){
            LOGGER.info("Some errors encountered when loading a file \""+ inputFile +"\"", e);
        }catch (IOException e){
            LOGGER.info("Some errors encountered when loading a file \""+ inputFile +"\"", e);
        }catch (ParseException e){
            LOGGER.info("Some errors encountered when parsing a file \""+ inputFile +"\"", e);
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

                    // get typeNEEntityFishing --> nerGrobidType
                    if (jsonObjectRow.get("type") != null) {
                        nerdEntity.setTypeNEGrobidNER(jsonObjectRow.get("type").toString());
                    } else {
                        nerdEntity.setTypeNEGrobidNER("");
                    }

                    // get nerKid_type
                    if (jsonObjectRow.get("typeKid") != null) {
                        nerdEntity.setTypeNENerdKid(jsonObjectRow.get("typeKid").toString());
                    } else {
                        nerdEntity.setTypeNENerdKid(null);
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

                    // get confidence_score_score which currently is actually
                    if (jsonObjectRow.get("confidence_score") != null) {
                        nerdEntity.setConfidenceScore(Double.parseDouble(jsonObjectRow.get("confidence_score").toString()));
                    } else {
                        nerdEntity.setConfidenceScore(0.0);
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
            LOGGER.info("Some errors encountered when parsing some Json strings.", e);
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
                    String[] data = {entity.getWikidataId(), entity.getTypeNEGrobidNER()};
                    csvWriter.writeNext(data);
                }
            }
            csvWriter.flush();
            csvWriter.close();

        } catch (IOException e) {
            LOGGER.info("Some errors encountered when saving to a Csv file in \""+ resultToSave +"\"", e);
        }
    }

    public void saveToFileCsvComplete(List<NerdEntity> resultToSave, String outputFile) {
        // get only the list of Wikidata Ids and Classes from the Json result
        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(outputFile), ',', CSVWriter.NO_QUOTE_CHARACTER);
            String[] header = {"rawName,wikipediaExternalRef,wikidataId,confidence_score,nerGrobid_type,nerKid_type"};
            csvWriter.writeNext(header);
            // get the list of Wikidata Ids and Class
            for (NerdEntity entity : resultToSave) {
                if (entity.getWikidataId() != null) {
                    String[] data = {entity.getRawName(),Integer.toString(entity.getWikipediaExternalRef()),entity.getWikidataId(),Double.toString(entity.getConfidenceScore()),entity.getTypeNEGrobidNER(),entity.getTypeNENerdKid()};
                    csvWriter.writeNext(data);
                }
            }
            csvWriter.flush();
            csvWriter.close();

        } catch (IOException e) {
            LOGGER.info("Some errors encountered when saving to a Csv file in \""+ resultToSave +"\"", e);
        }
    }

}
