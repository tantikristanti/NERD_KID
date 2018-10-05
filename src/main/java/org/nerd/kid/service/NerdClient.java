package org.nerd.kid.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.nerd.kid.extractor.grobidNer.WikidataIdClassExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class NerdClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(NerdClient.class);

    private String HOST = "http://cloud.science-miner.com/nerd/service";
    //    private String HOST = "localhost:8090/service";
    private String DISAMBIGUATE_SERVICE = "/disambiguate";
    private int PORT = -1;

    public NerdClient() {

    }

    public NerdClient(String host) {
        HOST = host;
    }


    public NerdClient(String host, int port) {
        HOST = host;
        PORT = port;
    }

    /*
    curl 'http://cloud.science-miner.com/nerd/service/disambiguate'
    -X POST -F
    "query={'shortText': 'Any short text is put here','language': { 'lang': 'en'},'nbest': 0,'customisation': 'generic' }"
    * */
    public String shortTextDisambiguate(String shorText, String language) {
        String result = null;
        try {
            final URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(this.HOST + DISAMBIGUATE_SERVICE)
                    .build();

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("shortText", shorText);
            if (language != null) {
                ObjectNode dataNode = mapper.createObjectNode();
                dataNode.put("lang", language);
                node.set("language", dataNode);
            }
            HttpPost httpPost = new HttpPost(uri);
            CloseableHttpClient httpResponse = HttpClients.createDefault();

            httpPost.setHeader("Content-Type", APPLICATION_JSON.toString());
            httpPost.setEntity(new StringEntity(node.toString()));
            CloseableHttpResponse closeableHttpResponse = httpResponse.execute(httpPost);

            if (closeableHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = IOUtils.toString(closeableHttpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
            }

        } catch (URISyntaxException e) {
            LOGGER.info("Some errors encountered when extracting data from Entity-Fishing API service of short text disambiguation.", e);
        } catch (UnsupportedEncodingException e) {
            LOGGER.info("Some errors encountered when extracting data from Entity-Fishing API service of short text disambiguation.", e);
        } catch (ClientProtocolException e) {
            LOGGER.info("Some errors encountered when extracting data from Entity-Fishing API service of short text disambiguation.", e);
        } catch (IOException e) {
            LOGGER.info("Some errors encountered when extracting data from Entity-Fishing API service of short text disambiguation.", e);
        }

        return result;
    }

    /*
    curl 'http://cloud.science-miner.com/nerd/service/disambiguate'
    -X POST -F
    "query={'text': 'Any text is put here','language': { 'lang': 'en'},'nbest': 0,'customisation': 'generic' }"
    * */

    public String textDisambiguate(String text, String language) {
        String result = null;
        Gson gson = new Gson();
        try {
            final URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(this.HOST + DISAMBIGUATE_SERVICE)
                    .build();

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("text", text);
            if (language != null) {
                ObjectNode dataNode = mapper.createObjectNode();
                dataNode.put("lang", language);
                node.set("language", dataNode);
            }
            HttpPost httpPost = new HttpPost(uri);
            CloseableHttpClient httpResponse = HttpClients.createDefault();

            httpPost.setHeader("Content-Type", APPLICATION_JSON.toString());
            httpPost.setEntity(new StringEntity(node.toString()));
            CloseableHttpResponse closeableHttpResponse = httpResponse.execute(httpPost);


            if (closeableHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = IOUtils.toString(closeableHttpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
            }

        } catch (URISyntaxException e) {
            LOGGER.info("Some errors encountered when extracting data from Entity-Fishing API service of text disambiguation.", e);
        } catch (UnsupportedEncodingException e) {
            LOGGER.info("Some errors encountered when extracting data from Entity-Fishing API service of text disambiguation.", e);
        } catch (ClientProtocolException e) {
            LOGGER.info("Some errors encountered when extracting data from Entity-Fishing API service of text disambiguation.", e);
        } catch (IOException e) {
            LOGGER.info("Some errors encountered when extracting data from Entity-Fishing API service of text disambiguation.", e);
        }

        return result;
    }

    public String toJson(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJsonString = gson.toJson(jsonObject);

        return prettyJsonString;
    }

    public void saveToFileJson(String resultToSave, String outputFile) {
        try {
            File fl = new File(outputFile);

            BufferedWriter result = new BufferedWriter(new FileWriter(fl));

            result.write(resultToSave);
            result.close();
        } catch (Exception e) {
            LOGGER.info("Some errors encountered when saving the result into \"" + outputFile +"\"", e);
        }
    }

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

        //System.out.println(result);
        //System.out.println("The disambiguation result from Entity-Fishing in JSON format is in " + fileOutputJson);
        System.out.println("The disambiguation result from Entity-Fishing in CSV format is in " + fileOutputCsv);
    }
}
