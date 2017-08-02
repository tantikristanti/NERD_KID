package smile;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;


public class mainSmile {
    public static void main(String[] args) throws Exception {

        BufferedReader br = null;
        File file_name_input = null, file_name_result = null;

        smileUsage sm = new smileUsage();

        System.out.println("Name of training data file : ");
        br = new BufferedReader(new InputStreamReader(System.in));
        file_name_input = new File(br.readLine());

        sm.loadData(file_name_input);
        sm.trainModel();

        sm.validationModelLOOCV();

    }
}
