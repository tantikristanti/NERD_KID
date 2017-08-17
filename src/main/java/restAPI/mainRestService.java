package restAPI;

//import com.scienceminer.nerd.utilities.NerdProperties;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class mainRestService {
    public static void main(String[] args) throws Exception {

        callRestAPI call = new callRestAPI();

        BufferedReader br = null;
        //String url, query;

        System.out.println("Accessing REST API Nerd");

        String[] command = new String[] {"curl","http://cloud.science-miner.com/nerd/service/disambiguate","-XPOST","-F","query={ \"termVector\": [ { \"term\" : \"computer science\", \"score\" : 0.3 }, { \"term\" : \"engine\", \"score\" : 0.1 } ], \"language\": { \"lang\": \"en\" }, \"resultLanguages\": [\"de\"], \"nbest\": 0, \"customisation\": \"generic\" }"};

        Process process = Runtime.getRuntime().exec(command);

        StringBuilder processOutput = new StringBuilder();
        try (BufferedReader processOutputReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));)
        {
            String readLine;
            while ((readLine = processOutputReader.readLine()) != null)
        {
            processOutput.append(readLine + System.lineSeparator());
        }            process.waitFor();
        }
        processOutput.toString().trim();

        System.out.println(processOutput);

    }
}
