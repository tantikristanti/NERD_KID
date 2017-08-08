package smile;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Scanner;

public class mainSmile {
    public static void main(String[] args) throws Exception {

        BufferedReader br = null;
        File file_name_input = null, file_name_result = null;
        int split = 0, response = 0;

        smileUsage sm = new smileUsage();

        // loading the model
        System.out.print("Name of training data file (/[path]/[fileName].arff) : ");
        br = new BufferedReader(new InputStreamReader(System.in));
        file_name_input = new File(br.readLine());

        // accepting the value of response index
        System.out.print("\nResponse index (number of features) : ");
        Scanner in = new Scanner(System.in);
        response = in.nextInt();
        sm.loadData(file_name_input, response);

        // splitting the model into training and testing data
        System.out.print("Percentage of training data (in %) : ");
        Scanner input = new Scanner(System.in);
        split = input.nextInt();
        //sm.splitModel(split);

        //sm.trainModel();

        System.out.print("Name of the result file (automatically in /result/Result_[fileName].txt): ");
        br = new BufferedReader(new InputStreamReader(System.in));
        file_name_result = new File(br.readLine());
        sm.splitModel(file_name_result, split);

    }
}
