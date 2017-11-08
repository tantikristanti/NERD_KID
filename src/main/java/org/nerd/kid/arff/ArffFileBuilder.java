package org.nerd.kid.arff;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lfoppiano on 21/09/16.
 */
public class ArffFileBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArffFileBuilder.class);
    List<AttributeType> types = new ArrayList<>();
    private Writer writer;
    private boolean hasHeader = false;
    private boolean hasBody = false;

    private ArffFileBuilder() {
    }

    /**
     * Create a new file -  generate the header right after.
     */
    public static ArffFileBuilder createNewFile(Path filePath) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(Files.createFile(filePath, new FileAttribute[0]).toFile(), false);
        } catch (FileAlreadyExistsException e) {
            LOGGER.info("File " + filePath.toString() + " exists. Removing it.");
            filePath.toFile().delete();
            os = new FileOutputStream(Files.createFile(filePath, new FileAttribute[0]).toFile(), false);
        }

        ArffFileBuilder instance = new ArffFileBuilder();
        instance.setWriter(new OutputStreamWriter(os, "UTF-8"));

        return instance;
    }

    public static int getResponseFieldIndex(Path trainingArfPath) {
//        Counter c = new Counter(0);
//        try (Stream<String> trainingExamples = Files.lines(trainingArfPath)) {
//
//            trainingExamples.forEach(l -> {
//                if (l.startsWith("@attribute")) {
//                    c.increment(1);
//                }
//                if (l.contains("class")) {
//                    return;
//                }
//            });
//        } catch (IOException e) {
//
//        }
//        return c.getValue() - 1;
        return 1;
    }

    /**
     * Append to an existing file - assuming the header is already present.
     */
    public static ArffFileBuilder appendToFile(Path filePath) throws IOException {
        OutputStream os = new FileOutputStream(filePath.toFile(), true);
        ArffFileBuilder instance = new ArffFileBuilder();

        instance.setWriter(new OutputStreamWriter(os, "UTF-8"));
        instance.setHasHeader(true);
        return instance;
    }

    /**
     * Create an empty instance (mostly for test purposes) where the writer can be
     * customized (maybe writing on a string instead of a file).
     */
    public static ArffFileBuilder emptyInstance(Writer writer) {
        final ArffFileBuilder instance = new ArffFileBuilder();
        instance.setWriter(writer);

        return instance;
    }

    public void close() {
        IOUtils.closeQuietly(writer);

    }

    public ArffFileBuilder header(String relationName) throws IOException {
        writer.append("@relation").append(" ").append(relationName).append("\n").append("\n");
        setHasHeader(true);

        return this;
    }

    /**
     * e.g:
     *
     * @ATTRIBUTE lastname {no, yes}
     * @ATTRIBUTE length real
     */
    public ArffFileBuilder attribute(String attributeName, AttributeType type) {
        if (hasBody) {
            throw new RuntimeException("Cannot add attribute definition, the header has been closed already. ");
        }
        try {
            writer.append("@attribute")
                    .append(" ")
                    .append(attributeName)
                    .append(" ")
                    .append(type.toString())
                    .append("\n");
            types.add(type);
        } catch (IOException e) {
            throw new RuntimeException("Cannot write arff file", e);
        }

        return this;
    }

    public ArffFileBuilder nominalAttribute(String attributeName, String... nominalSpecifications) {
        if (hasBody) {
            throw new RuntimeException("Cannot add attribute definition, the header has been closed already. ");
        }

        try {

            writer.append("@attribute")
                    .append(" ")
                    .append(attributeName)
                    .append(" ");

            writer.append("{");
            StringBuilder sb2 = new StringBuilder();
            Arrays.asList(nominalSpecifications).stream().forEach(specification -> sb2.append(specification).append(", "));
            final String nominalSpecificationString = sb2.toString();
            writer.append(nominalSpecificationString.substring(0, nominalSpecificationString.length() - 2));
            writer.append("}").append("\n");
            types.add(AttributeType.NOMINAL);

        } catch (IOException e) {
            throw new RuntimeException("Cannot write arff file", e);
        }

        return this;
    }

    public ArffFileBuilder addComment(String comment) {
        try {
            writer.append("% " + comment).append("\n");
        } catch (IOException e) {
            throw new RuntimeException("Cannot write comment on arff file", e);
        }
        return this;
    }

    public ArffFileBuilder addExample(List<String> data, String class1) {
        if (!hasHeader) {
            throw new RuntimeException("Cannot add values, the header is not present or closed. ");
        }
        StringBuilder line = new StringBuilder();

        for (int i = 0; i < types.size(); i++) {
            AttributeType type = types.get(i);
            if (type == AttributeType.NOMINAL) {
                String item = data.get(i) != null ? data.get(i) : "?";
                line.append(item).append(",");
            } else if (type == AttributeType.NUMERIC) {
                line.append(data.get(i).toString()).append(",");
            } else {
                throw new RuntimeException("Time to update this ArffFileBuilder");
            }
        }

//        data.forEach(value -> line.append(value.toString()).append(","));
        line.append(class1).append("\n");
        try {
            writer.append(line.toString());

        } catch (IOException e) {
            throw new RuntimeException("Cannot write arff file", e);
        }
        return this;
    }

    public void closeHeader(String valueAttributeName, String classValues) {
        try {

            writer.append("@attribute")
                    .append(" ")
                    .append(valueAttributeName)
                    .append(" ")
                    .append(classValues)
                    .append("\n");

            if (!hasBody) {
                writer.append("\n")
                        .append("@data").append("\n");
                hasBody = true;
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot write arff file", e);
        }
    }

    public void setWriter(Writer writer) {
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

    public enum AttributeType {
        NUMERIC("numeric"),
        NOMINAL("nominal"),
        STRING("string"),
        DATE("date");

        private String value;

        AttributeType(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }
}
