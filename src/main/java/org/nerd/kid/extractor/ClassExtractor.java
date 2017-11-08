package org.nerd.kid.extractor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClassExtractor {
    public List<String> loadClasses() throws Exception {
        return loadClasses(new FileInputStream("data/resource/class_mapper.csv"));
    }

    public List<String> loadClasses(InputStream inputStreamFeatureFile) throws IOException {
        List<String> classMap = new ArrayList<>();
        Reader classMapperIn = new InputStreamReader(inputStreamFeatureFile);
        Iterable<CSVRecord> recordsClasses = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(classMapperIn);

        for (CSVRecord recordClass : recordsClasses) {
            String claz = recordClass.get("Class");

            // in order to get unique class
            if (!classMap.contains(claz)) {
                classMap.add(claz); // if not, add as a new class
            }
        }
        return classMap;
    }

    public void printClass(List<String> result) {
        for (String item : result) {
            System.out.println(item);
        }
    }
}
