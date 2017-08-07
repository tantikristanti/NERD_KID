package smile;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Scanner;


public class mainSmile {
    public static void main(String[] args) throws Exception {

        BufferedReader br = null;
        File file_name_input = null, file_name_result = null;
        int split = 0;

        smileUsage sm = new smileUsage();

        // loading the model
        System.out.println("Name of training data file : ");
        br = new BufferedReader(new InputStreamReader(System.in));
        file_name_input = new File(br.readLine());

        sm.loadData(file_name_input);

        // splitting the model into training and testing data
        System.out.println("Percentage of training data (in %) : ");
        Scanner in = new Scanner(System.in);
        split = in.nextInt();
        //sm.splitModel(split);

        //sm.trainModel();

        System.out.println("Name of the result file : ");
        br = new BufferedReader(new InputStreamReader(System.in));
        file_name_result = new File(br.readLine());
        sm.splitModel(file_name_result, split);

    }
}
