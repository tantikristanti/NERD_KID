package org.nerd.kid.arff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ArffParser {
    public String[] readClassArff(File file) throws Exception{
        // variables
        String line = "";
        String startClassAttribute = "@ATTRIBUTE class {";
        String endClassAttribute ="}";
        String[] classData = null;
        String splitBy = ",";

        //read a file
        BufferedReader reader = new BufferedReader(new FileReader(file));

        // getting the data of class
        while((line = reader.readLine()) != null){
            if (line.startsWith("@ATTRIBUTE")){
                if(line.contains("class")){
                    // removing start and end of symbols of class
                    String result = line.replace(startClassAttribute,"").replace(endClassAttribute,"");
                    classData = result.split(splitBy);
                }
            }

        }
        reader.close();

        return classData;
    }

    public List<String> readPropertiesTrainingFile(File file) throws Exception{
        // read training file
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String nextLine;
        List<String> listProperties = new ArrayList<String>();
        String splitBy = " ";
        // getting the data of class
        while ((nextLine = reader.readLine()) != null) {
            if (nextLine.startsWith("@ATTRIBUTE")) {
                if (!nextLine.contains("class")) {
                    String[] result = nextLine.split(splitBy);
                    listProperties.add(result[1]);
                }
            }
        }

        // stop the reader buffer
        reader.close();

        //return the list of properties from the training file
        return listProperties;
    }
}
