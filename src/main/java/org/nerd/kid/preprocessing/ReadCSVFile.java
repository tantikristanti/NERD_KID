package org.nerd.kid.preprocessing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadCSVFile {
    public ArrayList<String> readCsv(String file) throws Exception{
        BufferedReader bufferedReader = null;
        String line = "";
        //String split = ","; // if there are some data in one line
        ArrayList<String> result = new ArrayList<String>();
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            while ((line = bufferedReader.readLine()) != null){
                //String[] data = line.split(split);
                //result.add(data[0]);
                result.add(line);
            }

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (bufferedReader != null){
                bufferedReader.close();
            }
        }
        return result;

    }

}
