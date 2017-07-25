package smile;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class mainSmile {
    public static void main(String[] args) throws Exception {
        smileUsage sm = new smileUsage();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Name of training data file : ");
        File file_name = new File(br.readLine());
        sm.loadData(file_name);
    }
}
