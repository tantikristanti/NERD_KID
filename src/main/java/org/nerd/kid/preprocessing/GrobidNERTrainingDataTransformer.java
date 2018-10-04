package org.nerd.kid.preprocessing;

import au.com.bytecode.opencsv.CSVWriter;
import org.nerd.kid.arff.TrainerGenerator;
import org.nerd.kid.data.WikidataElementInfos;
import org.nerd.kid.extractor.grobidNer.GrobidNerEntity;
import org.nerd.kid.extractor.grobidNer.MentionExtractor;
import org.nerd.kid.service.NerdKidPaths;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is meant to extract some Mentions and Classes from Grobid-Ner project
 * and to get some ambiguation results from Entity-Fishing Rest API
 */

public class GrobidNERTrainingDataTransformer {
    public static void main(String[] args) {
        // collecting the result from Grobid-Ner project
        //String pathInputGrobidNer = NerdKidPaths.DATA_XML + "/AnnotatedCorpus.xml";
        String pathOutputEntityFishingJson = NerdKidPaths.DATA_JSON + "/Result_EntityFishingShortTextDisambiguation.json";
        String pathOutputGrobidNerCsv = NerdKidPaths.DATA_CSV + "/GrobidNer/AnnotatedCorpusResult.csv";
        String pathOutputEntityFishingCsv = NerdKidPaths.DATA_CSV + "/toBeCorrected/NewElements.csv";
        MentionExtractor mentionExtractor = new MentionExtractor();
        TrainerGenerator trainerGenerator = new TrainerGenerator();
        CSVWriter csvWriter = null;
        try {
            // header's file
            csvWriter = new CSVWriter(new FileWriter(pathOutputGrobidNerCsv), ',', CSVWriter.NO_QUOTE_CHARACTER);
            String[] header = {"Mention,Class"};
            csvWriter.writeNext(header);
            csvWriter.flush();
            csvWriter.close();

            // iterate through all xml file from Grobid-Ner project
            final List<Path> xmlFiles = trainerGenerator.listFiles(Paths.get(NerdKidPaths.DATA_XML), "*.{xml}");

            // extract data from Grobid-Ner project
            List<WikidataElementInfos> training = new ArrayList<>();
            for (Path inputFile : xmlFiles) {
                mentionExtractor.loadMentionClassFromGrobidNerProject(inputFile, pathOutputGrobidNerCsv);
            }

            // get the list of mentions
            List<GrobidNerEntity> listMentions = mentionExtractor.loadMentionClassFromCsv(new File(pathOutputGrobidNerCsv));

            // save to Json file in case will be needed for evaluation purpose
            mentionExtractor.mentionDisambiguationToJson(listMentions, pathOutputEntityFishingJson);

            // get the disambiguation results by using Entity-Fishing Rest API and save it to Csv file
            mentionExtractor.mentionDisambiguationToCsv(listMentions, pathOutputEntityFishingCsv);

            System.out.println("The extraction result from Grobid-Ner project is in " + pathOutputGrobidNerCsv);
            System.out.println("The disambiguation result from Entity-Fishing in Json format is in " + pathOutputEntityFishingJson);
            System.out.println("The disambiguation result from Entity-Fishing in CSV format is in " + pathOutputEntityFishingCsv);

        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Error : " + e.getMessage());
        }
    }
}
