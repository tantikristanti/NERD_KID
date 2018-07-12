package org.nerd.kid.extractor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.nerd.kid.exception.NerdKidException;
import org.nerd.kid.service.NerdKidPaths;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClassExtractor {
    public List<String> loadClasses() {
        // get the classes from the list in the csv file
        //String path = NerdKidPaths.DATA_RESOURCE;
        //String fileClassMapper = "class_mapper.csv";

        String fileFeatureMapper = "/class_mapper.csv";
        InputStream inputStream = this.getClass().getResourceAsStream(fileFeatureMapper);
        //ClassLoader classLoader = getClass().getClassLoader();
        try {
            //File file = new File(classLoader.getResource(fileFeatureMapper).getFile());
            List<String> classMap = new ArrayList<>();
            //InputStream inputStream = new FileInputStream(file);
            Reader classMapperIn = new InputStreamReader(inputStream);
            Iterable<CSVRecord> recordsClasses = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(classMapperIn);

            for (CSVRecord recordClass : recordsClasses) {
                String claz = recordClass.get("Class");

                // in order to get unique class
                if (!classMap.contains(claz)) {
                    classMap.add(claz); // if not, add as a new class
                }
            }
            return classMap;
        } catch (FileNotFoundException e) {
            throw new NerdKidException("An exception occured while NerdKid is running.", e);
        } catch (IOException e) {
            throw new NerdKidException("An exception occured while NerdKid is running.", e);
        }
    }

    public void printClass(List<String> result) {
        for (String item : result) {
            System.out.println(item);
        }
    }
}
