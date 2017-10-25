package org.nerd.kid.rest;

import java.util.Scanner;

public class MainCallAPIWikidata {
    public static void main(String[] args) throws Exception {
        System.out.println("This process takes several minutes.");
        Scanner scanner = new Scanner(System.in);

        System.out.print("Input file, ex 'data/arff/Training.arff' : ");
        String inputFile = scanner.nextLine();

        APIWikidataCaller callWiki = new APIWikidataCaller();
        callWiki.appendNewTestData(inputFile);
    }
}