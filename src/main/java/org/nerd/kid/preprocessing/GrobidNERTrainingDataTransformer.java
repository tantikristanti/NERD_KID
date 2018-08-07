package org.nerd.kid.preprocessing;

import au.com.bytecode.opencsv.CSVWriter;
import org.nerd.kid.service.NerdKidPaths;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

        String pathInput = NerdKidPaths.DATA_XML + "/AnnotatedCorpus.xml";
        String pathOutput = NerdKidPaths.DATA_CSV + "/GrobidNer/AnnotatedCorpusResult.csv";

        DocumentBuilder dbBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        File fileXML = new File(pathInput);
        Document doc = dbBuilder.parse(fileXML);
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("ENAMEX");
        int total = nList.getLength();

        try {
            csvWriter = new CSVWriter(new FileWriter(pathOutput), ';', CSVWriter.NO_QUOTE_CHARACTER);
            // header's file
            String[] header = {"Mention;Class"};
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
        System.out.print("Result in " + pathOutput);
    }
}
