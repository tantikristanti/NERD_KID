package org.nerd.kid.rest;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;

public class CreateCSVPredictedResult {
    public void CreateNewCsvFile(String[][] matrix) throws Exception{
        PrintStream writerCsv = new PrintStream(new FileOutputStream("result/Predicted_Testing.csv"));
        writerCsv.print("WikidataID" + ";" + "labelWikidata" + ";" + "ClassNerd" + ";" + "PredictedClass;");

        //read a file for getting the properties names
        BufferedReader reader = new BufferedReader(new FileReader("data/Training.arff"));
        String nextLine;
        String splitBy = " ";

        while ((nextLine = reader.readLine()) != null) {
            if (nextLine.startsWith("@ATTRIBUTE")) {
                if (!nextLine.contains("class")) {
                    String[] result = nextLine.split(splitBy);
                    writerCsv.print(result[1]);
                    if (reader.readLine() != null){
                        writerCsv.print(";");
                    }
                }
            }
        }

        writerCsv.print("\n");
        // print the result
        for (int i=0;i<matrix.length;i++){
            for(int j=0;j<matrix[i].length;j++){
                writerCsv.print(matrix[i][j]);
                if (j != matrix[i].length - 1){
                    writerCsv.print(";");
                }
            }
            writerCsv.print("\n");
        }
        writerCsv.flush();
        writerCsv.close();
    }
}
