package restAPI;


import java.io.*;
import java.util.ArrayList;


public class callRestAPI {

    public void useCurl(String url, String query) throws Exception{

        System.out.println("Accessing REST API Nerd");

        // adding new elements of command in array list of string
        ArrayList<String> element = new ArrayList<String>();

        element.add("curl");
        element.add(url);
        //element.add("http://cloud.science-miner.com/nerd/service/disambiguate");
        element.add("-XPOST");
        element.add("-F");

        // checking the user input whether or not begin with "query="
        if (query.startsWith("query="))
            element.add(query);
        else
            element.add("query="+query);

        //element.add("query={ \"termVector\": [ { \"term\" : \"computer science\", \"score\" : 0.3 }, { \"term\" : \"engine\", \"score\" : 0.1 } ], \"language\": { \"lang\": \"en\" }, \"resultLanguages\": [\"de\"], \"nbest\": 0, \"customisation\": \"generic\" }");
        System.out.println(element);

        // converting array list of string to string array
        String[] command = element.toArray(new String[element.size()]);

        // executing the command
        Process process = Runtime.getRuntime().exec(command);

        // getting the result of execution
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
