package org.nerd.kid.arff;

import org.nerd.kid.extractor.FeatureWikidataExtractor;
import org.nerd.kid.preprocessing.CSVFileReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
This class contains methods to generate Arff file
* */

public class ArffGenerator {

    public void createNewArffFile(String fileTraining, String fileToBeAdded) throws Exception{
        // copy file training into a new one before add new elements
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            // file to be read
            inputStream = new FileInputStream(new File(fileTraining));
            outputStream = new FileOutputStream("data/TrainingNew.arff");

            byte[] buffer = new byte[1024];
            int length;
            // while input file is not null
            while ((length = inputStream.read(buffer))>0){
                outputStream.write(buffer,0,length);
            }
        }finally {
            inputStream.close();
            outputStream.close();
        }

        // get the feature of new WikidataId
        FeatureWikidataExtractor featureWikidataExtractor = new FeatureWikidataExtractor();
        String[][] matrixData = featureWikidataExtractor.getFeatureWikidata(new File(fileTraining), new File(fileToBeAdded));

        FileWriter fileWriter = new FileWriter("data/TrainingNew.arff", true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        try {
            bufferedWriter.write("\n");
            // print the result
            for (int i = 0; i < matrixData.length; i++) {
                /* firstly, put properties for each Wikidata Id
                properties start from column 3 until the size of properties*/
                for (int j = 3; j < matrixData[i].length; j++) {
                    bufferedWriter.write(matrixData[i][j]);

                    if (j != matrixData[i].length - 1) {
                        bufferedWriter.write(",");
                    }
                }
                bufferedWriter.write("\n");
            }
        }finally {
            bufferedWriter.close();
        }

    }

}
