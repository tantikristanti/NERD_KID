package org.nerd.kid.model;

import java.io.File;

/*
    main class to build a model from training file
    ex.
    Input from : data/arff/TrainingNew.arff
    Percentage of training data (in %), the rest for testing : 80
**/
public class MainModelBuilder {
    public static void main(String[] args) throws Exception {

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.loadData();

        int split = 80;
        System.out.print("Percentage of training data (in %): " + split);
        modelBuilder.splitModel(split);
        System.out.println("Result can be found in 'result/txt/Result_Trained_Model.txt'");

        final String pathname = "/tmp/model.xml";
        modelBuilder.saveModel(new File(pathname));
        System.out.println("Model has been saved in " + pathname);
    }
}
