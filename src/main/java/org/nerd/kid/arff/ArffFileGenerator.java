package org.nerd.kid.arff;


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


/*
This class contains methods to generate Arff file.
Methods are adapted from a class ArffFileBuilder.java built by Luca Foppiano @21/09/16.
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

    public ArffFileGenerator addAttribute(String attributeName) {
        if (hasBody) {
            throw new RuntimeException("Cannot add attribute definition. ");
        }
        try {
            writer.append("@ATTRIBUTE")
                    .append(" ")
                    .append(attributeName)
                    .append(" {0,1}")
                    .append("\n");
        } catch (IOException e) {
            throw new RuntimeException("Cannot write arff file.", e);
        }

        return this;
    }

    public void addClassHeader(String[] classValues) {
        try {
            int lastPosition = classValues.length;
            writer.append("@ATTRIBUTE class")
                    .append(" {");
            for (int i = 0; i < classValues.length; i++) {
                writer.append(classValues[i]);
                if (i != lastPosition) {
                    writer.append(",");
                } else {
                    writer.append("}");
                }
            }


            if (!hasBody) {
                writer.append("\n")
                        .append("@data").append("\n");
                hasBody = true;
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot write arff file", e);

        }
    }


    public ArffFileGenerator addData(List<String> data, String claz) {
        if (!hasHeader) {
            throw new RuntimeException("Cannot add values, the header is not present or closed. ");
        }
        StringBuilder line = new StringBuilder();

        for (int i = 0; i < data.size(); i++) {
            line.append(data.get(i).toString()).append(",");
        }
        line.append(claz).append("\n");
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
