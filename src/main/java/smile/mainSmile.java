package smile;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.log4j.BasicConfigurator;

public class mainSmile {
    public static void main(String[] args) throws Exception {

        BasicConfigurator.configure();

        BufferedReader br = null;

        smileUsage sm = new smileUsage();
        br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Name of training data file : ");
        File file_name = new File(br.readLine());
        sm.loadData(file_name);
        //sm.trainModel();
        sm.validationModelLOOCV();
    }


}
