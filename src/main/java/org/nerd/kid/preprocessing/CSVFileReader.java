package org.nerd.kid.preprocessing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
class to parse CSV files in order to get Wikidata Ids and their classes
* */

public class CSVFileReader {
    public ArrayList<String> readCsv(String file) throws Exception {
        BufferedReader bufferedReader = null;
        String line = "";
        ArrayList<String> result = new ArrayList<String>();
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            while ((line = bufferedReader.readLine()) != null) {
                result.add(line);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return result;

    }

    public ArrayList<String> readWikiIdCsv(String file) throws Exception {
        BufferedReader bufferedReader = null;
        String line = "";
        String splitBy = ";"; // if there are some data in one line
        ArrayList<String> result = new ArrayList<String>();
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(splitBy);
                //just put the Wikidata Ids
                if (line.charAt(0) == 'Q')
                    result.add(data[0]);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        return result;
    }

    public Map<String, ArrayList<String>> readWikiIdClassCsv(String file) throws Exception {
        BufferedReader bufferedReader = null;
        String line = "";
        String splitBy = ";"; // if there are some data in one line
        Map<String, ArrayList<String>> listDataCSV = new HashMap<String, ArrayList<String>>();
        ArrayList<String> dataCSVWikiId = new ArrayList<String>();
        ArrayList<String> dataCSVClass = new ArrayList<String>();
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(splitBy);
                //just put the Wikidata Ids and its class
                if (line.charAt(0) == 'Q') {
                    dataCSVWikiId.add(data[0]);
                    dataCSVClass.add(data[2]);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        //put the result into HashMap
        listDataCSV.put("WikidataId", dataCSVWikiId);
        listDataCSV.put("ClassNerd", dataCSVClass);

        return listDataCSV;
    }
}
