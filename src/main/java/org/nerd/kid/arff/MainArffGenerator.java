package org.nerd.kid.arff;

import java.util.Scanner;

/*
main class for generating new arff file
ex.

Input File Training File (*.arff) : data/model0/model0.arff
Input File Containing the List of Wikidata Ids : data/corpus/csv/text.wikipedia.1.csv

* */

public class MainArffGenerator {
    public static void main(String[] args) throws Exception{
        Scanner input = new Scanner(System.in);
        System.out.println("Add new testing data ...");

        System.out.print("Input File Training File (*.arff) : ");
        String input1= input.nextLine();

        System.out.print("Input File Containing the List of Wikidata Ids : ");
        String input2= input.nextLine();

        ArffGenerator arffGenerator = new ArffGenerator();
        arffGenerator.createNewArffFile(input1, input2);

        System.out.print("Result can be seen in 'data/arff/TrainingNew.arff' ");

    }
}
