package org.nerd.kid.arff;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.nerd.kid.data.WikidataElementInfos;
import org.nerd.kid.extractor.ClassExtractor;
import org.nerd.kid.extractor.FeatureFileExtractor;
import org.nerd.kid.extractor.FeatureDataExtractor;
import org.nerd.kid.extractor.wikidata.NerdKBFetcherWrapper;
import org.nerd.kid.service.NerdKidPaths;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/*
main class for generating Arff file
*/

public class TrainerGenerator {
    ArffFileGenerator arffFileGenerator = new ArffFileGenerator();
    NerdKBFetcherWrapper nerdKBFetcherWrapper = new NerdKBFetcherWrapper();
    FeatureDataExtractor featureWikidataExtractor = new FeatureDataExtractor(nerdKBFetcherWrapper);
    FeatureFileExtractor featureFileExtractor = new FeatureFileExtractor();
    ClassExtractor classExtractor = new ClassExtractor();


    public void run(String fileOutput) throws Exception {
        // get the list of features
        Map<String, List<String>> resultFeature = featureFileExtractor.loadFeatures();
        List<String> resultFeatureNoValue = featureFileExtractor.loadFeaturesNoValue();

        // get the list classes
        List<String> resultClass = classExtractor.loadClasses();

        // generate new training file of Arff
        arffFileGenerator.createNewFile();

        // add header
        arffFileGenerator.addHeader();

        // add attributes and class attribute
        arffFileGenerator.addAttributeNoValue(resultFeatureNoValue);
        arffFileGenerator.addAttribute(resultFeature);
        arffFileGenerator.addClassHeader(resultClass);

        // add data
        final List<Path> trainingFiles = listFiles(Paths.get(NerdKidPaths.DATA_CSV), "*.{csv}");
        List<WikidataElementInfos> training = new ArrayList<>();
        for (Path inputFile : trainingFiles) {
            // get all the WikidataId and Class lists in the csv file
            List<WikidataElementInfos> elements = extractData(inputFile.toFile());
            training.addAll(elements);
        }

        // iterate for every WikidataId got from the csv file to get the feature
        for (WikidataElementInfos element : training) {
            try {
                WikidataElementInfos wikidataFeatures = featureWikidataExtractor.getFeatureWikidata(element.getWikidataId());
                if (wikidataFeatures != null) {
                    wikidataFeatures.setRealClass(element.getRealClass());
                    arffFileGenerator.addSingle(wikidataFeatures);
                } else
                    continue;
            } catch (Exception e) {
                System.out.println("Some error encountered, skipping entity: " + element.getWikidataId());
            }
        }

        arffFileGenerator.close();

        System.out.print("Result can be seen in " + NerdKidPaths.RESULT_ARFF + "/" + fileOutput);
    }


    public List<WikidataElementInfos> extractData(File inputFile) throws Exception {
        // get all the data from Csv file containing fields of WikidataId and Class

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

        // send only the unique wiki Ids (no duplicate)
        Set<WikidataElementInfos> inputListUniqueProcess = new HashSet<>();
        inputListUniqueProcess.addAll(inputList);

        List<WikidataElementInfos> inputListUnique = new ArrayList(inputList);

        return inputListUnique;

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

    public void saveResultCsvFormat(String fileOutput) throws Exception {
        String csvDataPath = NerdKidPaths.RESULT_CSV  + "/" + fileOutput;
        CSVWriter csvWriter = null;
        // get the list of features
        Map<String, List<String>> resultFeature = featureFileExtractor.loadFeatures();
        List<String> resultFeatureNoValue = featureFileExtractor.loadFeaturesNoValue();

        try {
            csvWriter = new CSVWriter(new FileWriter(csvDataPath), ',', CSVWriter.NO_QUOTE_CHARACTER);

            // the header's file
            List<String> headerPredict = Arrays.asList("WikidataID,LabelWikidata,Class");
            List<String> headerPropertyNoValue = new ArrayList<String>();
            List<String> headerPropertyValue = new ArrayList<String>();
            List<String> headerCombined = new ArrayList<String>();

            // header contains properties of Wiki Ids
            for (String propertyNoValue : resultFeatureNoValue){
                headerPropertyNoValue.add(propertyNoValue);
            }

            for (Map.Entry<String, List<String>> property : resultFeature.entrySet()) {
                List<String> values = property.getValue();

                for (String item : values) {
                    String propertyValue = property.getKey() + "_" + item;
                    headerPropertyValue.add(propertyValue);
                }
            }

            headerCombined.addAll(headerPredict);
            headerCombined.addAll(headerPropertyNoValue);
            headerCombined.addAll(headerPropertyValue);
            csvWriter.writeNext(headerCombined.toArray(new String[headerCombined.size()]));

            // the data
            final List<Path> trainingFiles = listFiles(Paths.get(NerdKidPaths.DATA_CSV), "*.{csv}");
            List<WikidataElementInfos> training = new ArrayList<>();
            for (Path inputFile : trainingFiles) {
                List<WikidataElementInfos> elements = extractData(inputFile.toFile());
                training.addAll(elements);
            }

            for (WikidataElementInfos element : training) {

                WikidataElementInfos wikidataFeatures = featureWikidataExtractor.getFeatureWikidata(element.getWikidataId());
                wikidataFeatures.setRealClass(element.getRealClass());

                // replace commas in Wikidata labels with the underscore to avoid incorrect extraction in the Csv file
                String label = wikidataFeatures.getLabel();
                if (label.contains(",")) {
                    wikidataFeatures.setLabel(label.replace(",", "_"));
                }

                List<String> dataGenerated = Arrays.asList(wikidataFeatures.getWikidataId(), label, wikidataFeatures.getRealClass());
                List<String> dataFeatureGenerated = new ArrayList<String>();
                List<String> dataCombined = new ArrayList<String>();

                Double[] features = wikidataFeatures.getFeatureVector();
                for (Double feature : features) {
                    dataFeatureGenerated.add(feature.toString());
                }
                dataCombined.addAll(dataGenerated);
                dataCombined.addAll(dataFeatureGenerated);
                csvWriter.writeNext(dataCombined.toArray(new String[dataCombined.size()]));

            }
        } catch (Exception e) {
            System.out.println("Some error encountered, skipping entity.");
        } finally {
            csvWriter.flush();
            csvWriter.close();
        }
        System.out.print("Result in " +csvDataPath);

    }

    public static void main(String[] args) throws Exception {
        String fileOutputArff = "Training.arff";
        String fileOutputCsv = "ResultFromArffGenerator.csv";

        TrainerGenerator trainerGenerator = new TrainerGenerator();

        trainerGenerator.run(fileOutputArff);

        // create CSV file to check the result of data collected
        trainerGenerator.saveResultCsvFormat(fileOutputCsv);
    }
}
