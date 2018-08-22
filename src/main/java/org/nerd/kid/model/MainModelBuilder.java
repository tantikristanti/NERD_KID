package org.nerd.kid.model;

import org.nerd.kid.service.NerdKidPaths;

import java.io.File;

/*
    main class to build a model from training file
    ex.
    Input from : data/arff/Training.arff
    Percentage of training data (in %), the rest for testing : 80
**/
public class MainModelBuilder {
    public static void main(String[] args) throws Exception {
        final String pathnameXML = "/tmp/model.xml";
        final String pathnameZIP = "/tmp/model.zip";
        final String pathnameXMLExtracted = "/tmp/modelExtracted.xml";
        String fileInput = "Training.arff";
        String fileOutput = "Result_Trained_Model.txt";
        String pathInput = NerdKidPaths.RESULT_ARFF + "/" + fileInput;

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.loadData(new File(pathInput));

        int split = 80;
        System.out.print("Percentage of training data (in %): " + split);
        modelBuilder.splitModel(split);
        System.out.println("Result can be found in " + NerdKidPaths.RESULT_TXT + "/" + fileOutput);

        modelBuilder.saveModelToXML(new File(pathnameXML));
        byte[] resultInBytes= modelBuilder.readBytesFromFile(new File(pathnameXML));
        modelBuilder.createZip(resultInBytes, new File(pathnameZIP));
        System.out.println("Model has been saved in " + pathnameXML + " and " + pathnameZIP);
        modelBuilder.extractZip(new File(pathnameZIP), new File(pathnameXMLExtracted));
    }
}
