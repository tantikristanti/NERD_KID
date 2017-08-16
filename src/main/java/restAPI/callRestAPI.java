package restAPI;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;


public class callRestAPI {
    public void callRestService(String link, String query) throws Exception{
        URL url = new URL(link);

        // making a connection
        URLConnection urlConnection = url.openConnection();

        // setDoOutput : POST, setDoInput : GET
        urlConnection.setDoOutput(true);
        urlConnection.setAllowUserInteraction(false);

        // sending a query
        PrintStream printStream = new PrintStream(urlConnection.getOutputStream());
        printStream.print(query);
        printStream.close();

        // getting the result
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String result = null;
        while ((result = bufferedReader.readLine()) != null)
            System.out.println(result);

        bufferedReader.close();
    }
}
