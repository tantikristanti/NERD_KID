package org.nerd.kid.extractor.grobidNer;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.nerd.kid.exception.NerdKidException;
import org.nerd.kid.service.NerdClient;
import org.nerd.kid.service.NerdEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MentionExtractor {
    /* method for extracting list of mentions and their classes from Grobid-Ner project
    https://github.com/kermitt2/grobid-ner/blob/master/grobid-ner/resources/dataset/ner/corpus/xml/final
    * */
    public void loadMentionClassFromGrobidNerProject(Path fileInput, String fileOutput) {
        CSVWriter csvWriter = null;

        try {
            DocumentBuilder dbBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            //File fileXML = new File(fileInput);

            File fileXML = fileInput.toFile();
            Document doc = dbBuilder.parse(fileXML);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("ENAMEX");
            int total = nList.getLength();
            csvWriter = new CSVWriter(new FileWriter(fileOutput, true), ',', CSVWriter.NO_QUOTE_CHARACTER);

            for (int i = 0; i < total; i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String mention = element.getTextContent();
                    // replace all commas in mentions with semicolons
                    String mentionModified = mention.replace(",", ";");
                    mentionModified = mentionModified.replace("\"", "");
                    mentionModified = mentionModified.replace("\n", " ");
                    String claz = element.getAttribute("type");
                    if (claz == null){
                        claz = "";
                    }
                    String[] data = {mentionModified, claz};

                    csvWriter.writeNext(data);
                }
            }
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public List<GrobidNerEntity> loadMentionClassFromCsv(File inputFile) {

        List<GrobidNerEntity> listMentionClassMap = new ArrayList<>();

        try {
            Reader reader = new FileReader(inputFile);
            Iterable<CSVRecord> recordsOfMentions = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            for (CSVRecord record : recordsOfMentions) {
                GrobidNerEntity grobidNerEntity = new GrobidNerEntity();
                String mention = record.get("Mention");
                String claz = record.get("Class");
                if (mention != null) {
                    if (claz == null) {
                        claz = "";
                    }
                    // in order to get unique mention and class
                    if (!listMentionClassMap.contains(grobidNerEntity)) {
                        grobidNerEntity.setMention(mention);
                        grobidNerEntity.setClaz(claz);
                        listMentionClassMap.add(grobidNerEntity);
                    }
                }
            }
            return listMentionClassMap;
        } catch (FileNotFoundException e) {
            throw new NerdKidException("An exception occured while NerdKid is running.", e);
        } catch (IOException e) {
            throw new NerdKidException("An exception occured while NerdKid is running.", e);
        }
    }

    public void mentionDisambiguationToJson(List<GrobidNerEntity> mentionList, String outputFile) {
        // disambiguation of mentions with Entity-Fishing API rest, particularly short text disambiguation
        String result = null, lang = "en";

        int length = mentionList.size();
        try {
            NerdClient nerdClient = new NerdClient("cloud.science-miner.com/nerd/service");
            File fl = new File(outputFile);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fl));

            /* in order to be able to collect multiple Json object result,
            use [ and ] for the beginning and the end of the Json file, while each result is separated by commas */

            bufferedWriter.write("[");
            for (int i = 0; i < length; i++) {
                // short text disambiguation
                result = nerdClient.shortTextDisambiguate(mentionList.get(i).getMention(), lang);

                // saving the result
                String resultInJson = nerdClient.toJson(result);
                bufferedWriter.write(resultInJson);

                // if the result is not the last one, add comma
                if (i != length - 1) {
                    bufferedWriter.write(", \n");
                }
                System.out.println(result);
            }

            bufferedWriter.write("]");
            bufferedWriter.close();
            System.out.println("Result of disambiguation of Entity-Fishing can be seen in : " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mentionDisambiguationToCsv(List<GrobidNerEntity> mentionList, String outputFile) {
        // disambiguation of mentions with Entity-Fishing API rest, particularly short text disambiguation

        List<NerdEntity> listEntities = new ArrayList<>();
        String result = null, lang = "en";
        WikidataIdClassExtractor wikidataIdClassExtractor = new WikidataIdClassExtractor();

        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(outputFile), ',', CSVWriter.NO_QUOTE_CHARACTER);
            NerdClient nerdClient = new NerdClient("cloud.science-miner.com/nerd/service");

            // header of Csv file
            String[] header = {"RawText,WikidataID,Class,ClassEntityFishing,NerdScore,SelectionScore"};
            csvWriter.writeNext(header);

            for (GrobidNerEntity grobidNerEntity : mentionList) {
                // short text disambiguation
                result = nerdClient.shortTextDisambiguate(grobidNerEntity.getMention(), lang);

                // read the Json string result
                listEntities = wikidataIdClassExtractor.parseFromJsonString(result);

                // write to Csv file
                for (NerdEntity entity : listEntities) {
                    entity.setTypeNEGrobidNER(grobidNerEntity.getClaz());
                    if (entity.getWikidataId() != null) {
                        String[] data = {entity.getRawName(), entity.getWikidataId(), entity.getTypeNEGrobidNER(), entity.getTypeNEEntityFishing(), String.valueOf(entity.getNerdScore()), String.valueOf(entity.getSelectionScore())};
                        csvWriter.writeNext(data);
                    }
                }
            }
            csvWriter.flush();
            csvWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
