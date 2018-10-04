package org.nerd.kid.extractor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.nerd.kid.exception.NerdKidException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClassExtractor {
    public List<String> loadClasses() {
        // get the classes from the list in the csv file
        String fileFeatureMapper = "/class_mapper.csv";
        InputStream inputStream = this.getClass().getResourceAsStream(fileFeatureMapper);
        try {
            List<String> classMap = new ArrayList<>();
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
}
