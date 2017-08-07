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
        System.out.println("Name of training data file : ");
        br = new BufferedReader(new InputStreamReader(System.in));
        file_name_input = new File(br.readLine());
        System.out.println("Name of training data file : "+ file_name_input);

        System.out.println("Response index (model6_P31.arff: 52, model6_P31_P21.arff: 54, model6_P31_P21_P279: 59, model6_P31_PersonLocation: 52)");
        Scanner in = new Scanner(System.in);
        response = in.nextInt();
        sm.loadData(file_name_input, response);

        // splitting the model into training and testing data
        System.out.println("Percentage of training data (in %) : ");
        Scanner input = new Scanner(System.in);
        split = input.nextInt();
        //sm.splitModel(split);

        //sm.trainModel();

        System.out.println("Name of the result file : ");
        br = new BufferedReader(new InputStreamReader(System.in));
        file_name_result = new File(br.readLine());
        sm.splitModel(file_name_result, split);

    }
}
