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
import org.json.simple.parser.ParseException;
import org.nerd.kid.data.WikidataElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class NerdKBFetcherWrapper implements WikidataFetcherWrapper {

    String nerdUrl = "nerd.huma-num.fr";
    String nerdPath = "/nerd/service/kb/concept";

    @Override
    public WikidataElement getElement(String wikiId) throws Exception {

        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpHost target = new HttpHost(nerdUrl);
        HttpGet request = new HttpGet(nerdPath + "/" + wikiId);
        HttpResponse httpResponse = httpclient.execute(target, request);
        HttpEntity entity = httpResponse.getEntity();

        String response = IOUtils.toString(entity.getContent(), UTF_8);

        return fromJson(response);
    }

    public WikidataElement fromJson(String input) {
        JSONParser parser = new JSONParser();

        WikidataElement element = new WikidataElement();

        try {
            JSONObject object = (JSONObject) parser.parse(input);

            String rawName = (String) object.get("rawName");
            String preferredName = (String) object.get("preferredName");

            element.setLabel(rawName);

            JSONArray properties = (JSONArray) object.get("statements");

            final Map<String, List<String>> outputProperties = element.getProperties();

            for (int i = 0; i < properties.size(); i++) {
                final JSONObject o = (JSONObject) properties.get(i);

                String propertyId = (String) o.get("propertyId");
                String valueType = (String) o.get("valueType");
                if (!"string".equals(valueType)) {
                    continue;
                }
                String value = (String) o.get("value");
                if (value == null) {
                    continue;
                }

                if (outputProperties.get(propertyId) == null) {
                    outputProperties.put(propertyId, new ArrayList<>());
                }
                outputProperties.get(propertyId).add(value);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return element;

    }
}
