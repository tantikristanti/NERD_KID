package org.nerd.kid.extractor.wikidata;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class NerdKBFetcherWrapper implements WikidataFetcherWrapper {

    //String nerdPathLocal = "http://localhost:8090/service/kb/concept/";

    String nerdUrl = "nerd.huma-num.fr";
    //String nerdPath = "/nerd/service/kb/concept";
    String nerdPath = "/test/service/kb/concept";

    @Override
    public WikidataElement getElement(String wikiId) throws Exception {
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpHost target = new HttpHost(nerdUrl);
        HttpGet request = new HttpGet(nerdPath + "/" + wikiId);
        HttpResponse httpResponse = httpclient.execute(target, request);
        HttpEntity entity = httpResponse.getEntity();

        String response = null;
        // get the response Id for throw the exception if it's not OK == 200
        int responseId = httpResponse.getStatusLine().getStatusCode();

        if (responseId == 200) {
            response = IOUtils.toString(entity.getContent(), UTF_8);
            if (response.contains("wikidataId")) {
                return fromJson(response);
            } else {
                throw new DataException("Data parsing exception.");
            }
        } else {
            throw new RemoteServiceException("Remote service exception.");
        }
    }

    public WikidataElement fromJson(String input) throws Exception {
        JSONParser parser = new JSONParser();

        WikidataElement element = new WikidataElement();

        JSONObject object = (JSONObject) parser.parse(input);

        String rawName = (String) object.get("rawName");
        String preferredName = (String) object.get("preferredName");

        // replace commas in Wikidata labels with the underscore to avoid incorrect extraction in the Csv file
        if (rawName.contains(",")) {
            rawName = rawName.replace(",", "_");
        }

        element.setLabel(rawName);

        JSONArray properties = (JSONArray) object.get("statements");

        final Map<String, List<String>> outputProperties = element.getProperties();

        for (int i = 0; i < properties.size(); i++) {
            final JSONObject o = (JSONObject) properties.get(i);

            String propertyId = (String) o.get("propertyId");
            String valueType = (String) o.get("valueType");
            if (!"wikibase-item".equals(valueType) && !"string".equals(valueType)) {
                continue;
            }
            Object value = (Object) o.get("value");
            Object valueObject = (Object) o.get("value");

            if (value == null || valueObject == null) {
                continue;
            }

            if (outputProperties.get(propertyId) == null) {
                outputProperties.put(propertyId, new ArrayList<>());
            }
            outputProperties.get(propertyId).add(value.toString());
        }

        return element;

    }

}
