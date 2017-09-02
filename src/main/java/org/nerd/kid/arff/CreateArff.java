package org.nerd.kid.arff;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CreateArff {
    public void CreateNewArffFile() throws Exception{
        BufferedReader reader = new BufferedReader(new FileReader("data/Training.arff"));
        PrintStream writer = new PrintStream(new FileOutputStream("data/Testing.arff"));
        CreateNewArffFile(reader, writer);
    }

    public void CreateNewArffFile(BufferedReader reader, PrintStream writerArff) throws Exception{
        // read a file
        String nextLine;
        List<String> listProperties = new ArrayList<String>();
        String splitBy = " ";

        // put to a new file
        writerArff.println("@RELATION Testing\n");
        // getting the data of class
        while ((nextLine = reader.readLine()) != null) {
            if (nextLine.startsWith("@ATTRIBUTE")) {
                writerArff.println(nextLine);
                if (!nextLine.contains("class")) {
                    String[] result = nextLine.split(splitBy);
                    listProperties.add(result[1]);
                }
            }
        }

        writerArff.println("\n@DATA");

        // stop the reader buffer
        reader.close();

    }

}
