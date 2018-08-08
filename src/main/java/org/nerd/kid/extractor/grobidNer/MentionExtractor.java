package org.nerd.kid.extractor.grobidNer;

import com.google.common.base.Joiner;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.nerd.kid.exception.NerdKidException;
import org.nerd.kid.service.NerdClient;
import org.nerd.kid.service.NerdKidPaths;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MentionExtractor {
    public List<String> loadMention(File inputFile) {

        List<String> mentionMap = new ArrayList<>();

        try {
            Reader reader = new FileReader(inputFile);
            Iterable<CSVRecord> recordsOfMentions = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            for (CSVRecord record : recordsOfMentions) {
                String mention = record.get("Mention");

                // in order to get unique mention
                if (!mentionMap.contains(mention)) {
                    mentionMap.add(mention);
                }
            }
            return mentionMap;
        } catch (FileNotFoundException e) {
            throw new NerdKidException("An exception occured while NerdKid is running.", e);
        } catch (IOException e) {
            throw new NerdKidException("An exception occured while NerdKid is running.", e);
        }
    }

    public void printResult(List<String> result) {
        for (String item : result) {
            System.out.println(item);
        }
    }

    public void mentionDisambiguation(List<String> mentionList){
        // disambiguation of mentions with Entity-Fishing API rest, particularly short text disambiguation
        String result = null, lang = "en";
        String outputFile = "data/json/Result_EntityFishingShortTextDisambiguation.json";

        try {
            NerdClient entityFishingService = new NerdClient("cloud.science-miner.com/nerd/service");
            File fl = new File(outputFile);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fl));

            /* in order to be able to collect multiple Json object result,
            use [ and ] for the beginning and the end of the Json file, while each result is separated by commas */

            bufferedWriter.write("[");
            for (Iterator<String> iterator = mentionList.iterator(); iterator.hasNext();) {
                String item = iterator.next();

                // short text disambiguation
                result = entityFishingService.shortTextDisambiguate(item, lang);

                // saving the result
                String resultInJson = entityFishingService.toJson(result);

                // if the result is not the last one, add comma
                bufferedWriter.write(resultInJson);
                if (iterator.hasNext()){
                    bufferedWriter.write(", \n");
                }
                System.out.println(result);
            }

            bufferedWriter.write("]");
            bufferedWriter.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        // get the mentions from the GrobidNer extraction result
        String path = NerdKidPaths.DATA_CSV;
        String fileCorpusResult = path + "/GrobidNer/AnnotatedCorpusResult.csv";
        MentionExtractor mentionExtractor = new MentionExtractor();
        List<String> result = mentionExtractor.loadMention(new File(fileCorpusResult));
        mentionExtractor.printResult(result);
        mentionExtractor.mentionDisambiguation(result);
    }
}
