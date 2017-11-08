package org.nerd.kid.arff;


import org.nerd.kid.data.WikidataElementInfos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;


/*
This class contains methods to generate Arff file.
Methods are adapted and modified from a class ArffFileBuilder.java built by Luca Foppiano @21/09/16.
* */

public class ArffFileGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArffFileBuilder.class);
    private BufferedWriter writer;
    private boolean hasHeader = false;
    private boolean hasBody = false;

    // to create new file
    public void createNewFile() throws Exception {
        createNewFile(FileSystems.getDefault().getPath("result/arff/Training.arff"));
    }

    public void createNewFile(Path path) throws Exception {
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toString(), false), StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOGGER.info("File " + path.toString() + " exists. Removing it.");
            path.toFile().delete();
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toString(), false), StandardCharsets.UTF_8));
        }
    }

    // to add data in an existing file
    public void appendToFile(Path path) throws Exception {
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toString(), true), StandardCharsets.UTF_8));
    }

    public ArffFileGenerator addHeader() throws IOException {
        writer.append("@RELATION").append(" Training").append("\n").append("\n");
        setHasHeader(true);

        return this;
    }

    public ArffFileGenerator addAttribute(Map<String, List<String>> attributeList) {
        if (hasBody) {
            throw new RuntimeException("Cannot add attribute definition. ");
        }
        try {

            for (Map.Entry<String, List<String>> property : attributeList.entrySet()) {
                List<String> values = property.getValue();
                for (String item : values) {
                    String propertyValue = property.getKey() + "_" + item;
                    writer.append("@ATTRIBUTE")
                            .append(" ")
                            .append(propertyValue)
                            .append(" {0,1}")
                            .append("\n");
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Cannot write arff file.", e);
        }

        return this;
    }


    public ArffFileGenerator addClassHeader(List<String> classValues) {
        try {
            int lastPosition = classValues.size() - 1;
            writer.append("@ATTRIBUTE class")
                    .append(" {");
            for (int i = 0; i < classValues.size(); i++) {
                writer.append(classValues.get(i));
                if (i != lastPosition) {
                    writer.append(",");
                } else {
                    writer.append("}");
                }
            }


            if (!hasBody) {
                writer.append("\n\n")
                        .append("@DATA").append("\n");
                hasBody = true;
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot write arff file", e);

        }
        return this;
    }


    public ArffFileGenerator addData(List<WikidataElementInfos> matrixWikidataElement) {
        if (!hasHeader) {
            throw new RuntimeException("Cannot add values, the header is not present or closed. ");
        }
        StringBuilder line = new StringBuilder();

//        for (int i = 0; i < data.size(); i++) {
//            line.append(data.get(i).toString()).append(",");
//        }
//        line.append(claz).append("\n");
        try {
            writer.append(line.toString());

        } catch (IOException e) {
            throw new RuntimeException("Cannot write arff file", e);
        }
        return this;
    }


    public void close() throws Exception {
        writer.close();

    }

    public void setWriter(BufferedWriter writer) {
        this.writer = writer;
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    public boolean hasHeader() {
        return hasHeader;
    }

    public boolean hasBody() {
        return hasBody;
    }

}
