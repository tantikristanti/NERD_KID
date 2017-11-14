package org.nerd.kid.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Scanner;

/*
main class to build a model from training file
ex.
Input from : data/arff/TrainingNew.arff
Percentage of training data (in %), the rest for testing : 80

* */
public class MainModelBuilder {
    public static void main(String[] args) throws Exception {

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.loadData();

        // splitting the model into training and testing data
        System.out.print("Percentage of training data (in %), the rest for testing : ");
        Scanner input = new Scanner(System.in);
        Integer split = input.nextInt();
        modelBuilder.splitModel(split);
        System.out.println("Result can be seen in 'result/txt/Result_Trained_Model.txt'");
    }
}
