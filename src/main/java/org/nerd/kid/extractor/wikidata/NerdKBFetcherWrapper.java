package org.nerd.kid.extractor.wikidata;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.nerd.kid.data.WikidataElement;
import org.nerd.kid.exception.DataException;
import org.nerd.kid.exception.RemoteServiceException;
import org.nerd.kid.extractor.grobidNer.WikidataIdClassExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class NerdKBFetcherWrapper implements WikidataFetcherWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(NerdKBFetcherWrapper.class);

    //String nerdPathLocal = "http://localhost:8090/service/kb/concept/";
    //String nerdUrl = "nerd.huma-num.fr";
    //String nerdPath = "/service/kb/concept";
    //String nerdPath = "/test/service/kb/concept";
    String urlNerd = "http://nerd.huma-num.fr/nerd/service/kb/concept";

    @Override
    public WikidataElement getElement(String wikiId) {
        WikidataElement result = null;
        try {
            HttpClient client = HttpClientBuilder.create().build();
//        HttpHost target = new HttpHost(nerdUrl);
//        HttpGet request = new HttpGet(nerdPath + "/" + wikiId);
//        HttpResponse httpResponse = httpclient.execute(target, request);
            HttpGet request = new HttpGet(urlNerd + "/" + wikiId);
            HttpResponse httpResponse = client.execute(request);
            HttpEntity entity = httpResponse.getEntity();

            String response = null;
            // get the response Id for throw the exception if it's not OK == 200
            int responseId = httpResponse.getStatusLine().getStatusCode();
            if(responseId == 404){
                throw new RuntimeException("Run time exception.");
            }
            if (responseId == 200) {
                response = IOUtils.toString(entity.getContent(), UTF_8);
                if (response.contains(wikiId)) {
                    result = fromJson(response);
                    return result;
                }else {
                    throw new DataException("Data parsing exception.");
                }
            } else {
                throw new RemoteServiceException("Remote service exception.");
            }
        }catch (IOException e) {
            LOGGER.info("Some errors encountered when loading a file.", e);
        }catch (RemoteServiceException e) {
            LOGGER.info("Some errors encountered from remote service.", e);
        } catch (DataException e) {
            LOGGER.info("Some errors encountered from data loading.", e);
        }catch (Exception e){
            LOGGER.info("Some errors encountered when extracting Json string.", e);
        }
        return result;
    }

    public WikidataElement fromJson(String inputInJson) throws Exception {
        JSONParser parser = new JSONParser();

        WikidataElement element = new WikidataElement();

        JSONObject object = (JSONObject) parser.parse(inputInJson);

        String wikidataId = (String) object.get("wikidataId");
        String rawName = (String) object.get("rawName");
        //String preferredName = (String) object.get("preferredName");

        // replace commas in Wikidata labels with the underscore to avoid incorrect extraction in the Csv file
        rawName = rawName.replace(",", ";");
        rawName = rawName.replace("\"", "");
        rawName = rawName.replace("\'", "");

        element.setId(wikidataId);
        element.setLabel(rawName);

        JSONArray properties = (JSONArray) object.get("statements");

        Map<String, List<String>> outputProperties = element.getProperties();

        if (properties != null) {

            for (int i = 0; i < properties.size(); i++) {
                final JSONObject obj = (JSONObject) properties.get(i);

                String propertyId = (String) obj.get("propertyId");

                String value = (String) obj.get("value").toString();

                if (value == null) {
                    continue;
                }

                // check if property already exists in the map, just add the value because it's possible that a properties has several values
                if (outputProperties.containsKey(propertyId)) {
                    outputProperties.get(propertyId).add(value);
                } else {
                    List<String> values = new ArrayList<>();
                    values.add(value);
                    outputProperties.put(propertyId, values);
                }
                element.setProperties(outputProperties);

            }
        } else {
            element.setProperties(new HashMap<>());
        }
        return element;
    }
}
