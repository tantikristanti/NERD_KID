package preprocessing;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

public class OpenXMLFileGrobidNer {
    public static void main(String[] args) throws Exception {
        // object of CreateCSVFile
        CreateCSVFIle createCSVFIle = new CreateCSVFIle();

        // object of "DocumentBuilderFactory"
        DocumentBuilder dbBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        // création d'un parseur et création d'un document
        File file = new File("data/xml/annotatedCorpus.xml");
        Document doc = dbBuilder.parse(file);

        // normalizer le structure de XML
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("ENAMEX");
        int total = nList.getLength();
        String csvFile = "result/resultCSVAnnotatedCorpus.csv";
        FileWriter writer = new FileWriter(csvFile);
        createCSVFIle.writeLine(writer, Arrays.asList("Class;Entity"));
        for (int i = 0; i < total; i++) {
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                System.out.println(element.getAttribute("type") + ";" + element.getTextContent());

                // création de fichier CSV
                createCSVFIle.writeLine(writer, Arrays.asList(element.getAttribute("type") + ";" + element.getTextContent()));
            }
        }
        writer.flush();
        writer.close();
    }

}
