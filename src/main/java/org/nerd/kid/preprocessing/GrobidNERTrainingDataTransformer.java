package org.nerd.kid.preprocessing;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

/**
 * Transform the grobid ner training data in a csv file with
 * CLASS, mention
 * <p>
 * mention is the raw information from the text e.g. 'LEGAL, Washington Act'.
 */
public class GrobidNERTrainingDataTransformer {
    public static void main(String[] args) throws Exception {
        CSVFIleWriter csvFileWriter = new CSVFIleWriter();

        DocumentBuilder dbBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        File file = new File("data/xml/annotatedCorpus.xml");
        Document doc = dbBuilder.parse(file);

        // normalizer the XML's structure
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("ENAMEX");
        int total = nList.getLength();
        String csvFile = "result/resultCSVAnnotatedCorpus.csv";

        FileWriter writer = new FileWriter(csvFile);
        try {
            csvFileWriter.writeLine(writer, Arrays.asList("Class;Entity"));

            for (int i = 0; i < total; i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    System.out.println(element.getAttribute("type") + ";" + element.getTextContent());

                    // cxreation of CSV file
                    csvFileWriter.writeLine(writer, Arrays.asList(element.getAttribute("type") + ";" + element.getTextContent()));
                }
            }

        } finally {
            writer.flush();
            writer.close();
        }
    }

}
