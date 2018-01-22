package org.nerd.kid.arff;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.nerd.kid.data.WikidataElement;
import org.nerd.kid.data.WikidataElementInfos;
import org.nerd.kid.extractor.ClassExtractor;
import org.nerd.kid.extractor.FeatureFileExtractor;
import org.nerd.kid.extractor.FeatureWikidataExtractor;
import org.nerd.kid.extractor.wikidata.NerdKBFetcherWrapper;
import org.nerd.kid.extractor.wikidata.WikidataFetcherWrapper;

import javax.xml.bind.SchemaOutputResolver;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
main class for generating Arff file

It is possible to change the list of features and the list of classes

The list of features can be found in 'data/resource/feature_mapper.csv'
The list of classes can be found in 'data/resource/class_mapper.csv'

* */

public class MainTrainerGenerator {
    ArffFileGenerator arffFileGenerator = new ArffFileGenerator();
    NerdKBFetcherWrapper nerdKBFetcherWrapper = new NerdKBFetcherWrapper();
    FeatureWikidataExtractor featureWikidataExtractor = new FeatureWikidataExtractor(nerdKBFetcherWrapper);
    FeatureFileExtractor featureFileExtractor = new FeatureFileExtractor();
    ClassExtractor classExtractor = new ClassExtractor();

    public static void main(String[] args) throws Exception {
        MainTrainerGenerator mainTrainerGenerator = new MainTrainerGenerator();

        mainTrainerGenerator.run();

        // create CSV file to check the result of data collected
        mainTrainerGenerator.saveResultCsvFormat();
    }

    public void run() throws Exception {

        // get the list of features
        Map<String, List<String>> resultFeature = featureFileExtractor.loadFeatures();

        // get the list classes
        List<String> resultClass = classExtractor.loadClasses();

        // generate new training file of Arff
        arffFileGenerator.createNewFile();

        // add header
        arffFileGenerator.addHeader();

        // add attributes and class attribute
        arffFileGenerator.addAttribute(resultFeature);
        arffFileGenerator.addClassHeader(resultClass);

        // add data
        final List<Path> trainingFiles = listFiles(Paths.get("data/csv"), "*.{csv}");
        List<WikidataElementInfos> training = new ArrayList<>();
        for (Path inputFile : trainingFiles) {
            List<WikidataElementInfos> elements = extractData(inputFile.toFile());
            training.addAll(elements);
        }

        for (WikidataElementInfos element : training) {
            try {
                WikidataElementInfos wikidataFeatures = featureWikidataExtractor.getFeatureWikidata(element.getWikidataId());
                wikidataFeatures.setRealClass(element.getRealClass());
                arffFileGenerator.addSingle(wikidataFeatures);
            } catch (Exception e) {
                System.out.println("Some error encountered, skipping entity: " + element.getWikidataId());
            }
        }

        arffFileGenerator.close();

        System.out.print("Result can be seen in 'result/arff/Training.arff' ");
    }


    public List<WikidataElementInfos> extractData(File inputFile) throws Exception {
        List<WikidataElementInfos> inputList = new ArrayList<>();

        Reader reader = new FileReader(inputFile);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records) {
            String wikidataId = record.get("WikidataID");
            String realClass = record.get("Class");

            WikidataElementInfos wikidataElementInfos = new WikidataElementInfos();
            wikidataElementInfos.setRealClass(realClass);
            wikidataElementInfos.setWikidataId(wikidataId);
            inputList.add(wikidataElementInfos);

        } // end of looping to read file that contains Wikidata Id and class

        return inputList;
    }

    public static List<Path> listFiles(Path dir, String type) throws IOException {
        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, type)) {
            for (Path entry : stream) {
                result.add(entry);
            }
        } catch (DirectoryIteratorException ex) {
            throw ex.getCause();
        }
        return result;
    }

    public void saveResultCsvFormat() throws Exception {
        String csvDataPath = "result/csv/ResultFromArffGenerator.csv";
        CSVWriter csvWriter = null;
        // get the list of features
        Map<String, List<String>> resultFeature = featureFileExtractor.loadFeatures();

        try {
            csvWriter = new CSVWriter(new FileWriter(csvDataPath), ',', CSVWriter.NO_QUOTE_CHARACTER);
            // the header's file
            String[] headerPredict = {"WikidataID,LabelWikidata,Class"};
            csvWriter.writeNext(headerPredict);
            List<String> listPropertyValue = new ArrayList<String>();
            for (Map.Entry<String, List<String>> property : resultFeature.entrySet()) {
                List<String> values = property.getValue();

                for (String item : values) {
                    String propertyValue = property.getKey() + "_" + item;
                    listPropertyValue.add(propertyValue);
                }
                csvWriter.writeNext(listPropertyValue.toArray(new String[listPropertyValue.size()]));
            }

            // the data
            final List<Path> trainingFiles = listFiles(Paths.get("data/csv"), "*.{csv}");
            List<WikidataElementInfos> training = new ArrayList<>();
            for (Path inputFile : trainingFiles) {
                List<WikidataElementInfos> elements = extractData(inputFile.toFile());
                training.addAll(elements);
            }

            for (WikidataElementInfos element : training) {

                WikidataElementInfos wikidataFeatures = featureWikidataExtractor.getFeatureWikidata(element.getWikidataId());
                wikidataFeatures.setRealClass(element.getRealClass());

                //write the result into a Csv file
                String[] dataGenerated = {wikidataFeatures.getWikidataId(), wikidataFeatures.getLabel(), wikidataFeatures.getRealClass()};
                csvWriter.writeNext(dataGenerated);

                List<String> dataFeatureGenerated = new ArrayList<String>();

                Integer[] features = wikidataFeatures.getFeatureVector();
                for (Integer feature : features) {
                    dataFeatureGenerated.add(feature.toString());
                }
                csvWriter.writeNext(dataFeatureGenerated.toArray(new String[dataFeatureGenerated.size()]));

            }
        } catch (Exception e) {
            System.out.println("Some error encountered, skipping entity.");
        } finally {
            csvWriter.flush();
            csvWriter.close();
        }
        System.out.print("Result in 'result/csv/ResultFromArffGenerator.csv'");

    }
}
