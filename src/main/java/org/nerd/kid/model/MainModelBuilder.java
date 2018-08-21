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
        String fileInput = "Training.arff";
        String fileOutput = "Result_Trained_Model.txt";
        String pathInput = NerdKidPaths.RESULT_ARFF + "/" + fileInput;

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.loadData(new File(pathInput));

        int split = 80;
        System.out.print("Percentage of training data (in %): " + split);
        modelBuilder.splitModel(split);
        System.out.println("Result can be found in " + NerdKidPaths.RESULT_TXT + "/" + fileOutput);

        final String pathname = "/tmp/model.xml";
        modelBuilder.saveModel(new File(pathname));
        System.out.println("Model has been saved in " + pathname);
    }
}
