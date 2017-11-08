package org.nerd.kid.preprocessing;

import au.com.bytecode.opencsv.CSVWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;

/**
 * Extract Mentions and Classes from Grobid-Ner's data in XML format
 * Mention is the raw information from the text e.g. 'LEGAL, Washington Act'.
 */
public class GrobidNERTrainingDataTransformer {
    public static void main(String[] args) throws Exception {

        CSVWriter csvWriter = null;

        String xmlDataPath = "data/xml/annotatedCorpus.xml";
        String csvDataPath = "data/csv/annotatedCorpusResult.csv";

        DocumentBuilder dbBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        File fileXML = new File(xmlDataPath);
        Document doc = dbBuilder.parse(fileXML);
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("ENAMEX");
        int total = nList.getLength();

        try {
            csvWriter = new CSVWriter(new FileWriter(csvDataPath), ',', CSVWriter.NO_QUOTE_CHARACTER);
            // header's file
            String[] header = {"Mention,Class"};
            csvWriter.writeNext(header);

            for (int i = 0; i < total; i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String[] data = {element.getTextContent(),element.getAttribute("type")};
                    csvWriter.writeNext(data);
                }
            }

        } finally {
            csvWriter.flush();
            csvWriter.close();
        }
        System.out.print("Result in 'data/csv/annotatedCorpusResult.csv'");
    }
}
