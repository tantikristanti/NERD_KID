package org.nerd.kid.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {

        BufferedReader br = null;
        File file_name_train = null, file_name_test = null, file_name_result = null;
        int split = 0;

        KidService sm = new KidService();

        // loading the training data
        System.out.print("Name of training data file ([path]/[fileName].org.nerd.kid.arff) : ");
        br = new BufferedReader(new InputStreamReader(System.in));
        file_name_train = new File(br.readLine());

        // loading the train model
        sm.loadData(file_name_train);

        // splitting the model into training and testing data
        System.out.print("Percentage of training data (in %), org.nerd.kid.rest for testing : ");
        Scanner input = new Scanner(System.in);
        split = input.nextInt();
        System.out.print("Name of the result file in \"/result/Result_[fileName].txt\": ");
        file_name_result = new File(br.readLine());
        sm.splitModel(file_name_train, file_name_result, split);
    }
}