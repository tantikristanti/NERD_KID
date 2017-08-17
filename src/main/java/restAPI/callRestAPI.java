package restAPI;


import jdk.nashorn.internal.parser.JSONParser;
import sun.misc.IOUtils;

import javax.xml.ws.Response;
import java.io.*;
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

    public void useCurl() throws Exception{
        /**
        // making a connection
        URL url = new URL("http://cloud.science-miner.com/nerd/service/disambiguate");
        HttpURLConnection connect = (HttpURLConnection) url.openConnection();

        // POST method
        connect.setRequestMethod("POST");

        String data = "\"query={ 'text': 'Austria invaded and fought the Serbian army at the Battle of Cer and Battle of Kolubara beginning on 12 August. \\n\\nThe army, led by general Paul von Hindenburg defeated Russia in a series of battles collectively known as the First Battle of Tannenberg (17 August – 2 September). But the failed Russian invasion, causing the fresh German troops to move to the east, allowed the tactical Allied victory at the First Battle of the Marne. \\n\\nUnfortunately for the Allies, the pro-German King Constantine I dismissed the pro-Allied government of E. Venizelos before the Allied expeditionary force could arrive. Beginning in 1915, the Italians under Cadorna mounted eleven offensives on the Isonzo front along the Isonzo River, northeast of Trieste.\\n\\n At the Siege of Maubeuge about 40000 French soldiers surrendered, at the battle of Galicia Russians took about 100-120000 Austrian captives, at the Brusilov Offensive about 325 000 to 417 000 Germans and Austrians surrendered to Russians, at the Battle of Tannenberg 92,000 Russians surrendered.\\n\\n After marching through Belgium, Luxembourg and the Ardennes, the German Army advanced, in the latter half of August, into northern France where they met both the French army, under Joseph Joffre, and the initial six divisions of the British Expeditionary Force, under Sir John French. A series of engagements known as the Battle of the Frontiers ensued. Key battles included the Battle of Charleroi and the Battle of Mons. In the former battle the French 5th Army was almost destroyed by the German 2nd and 3rd Armies and the latter delayed the German advance by a day.', 'processSentence': [ 1 ], 'sentences': [ { 'offsetStart': 0, 'offsetEnd': 138 }, { 'offsetStart': 138, 'offsetEnd': 293 } ], 'entities': [ ] }\"";
        connect.setRequestProperty("Content-length", String.valueOf(data.length()));

        // sending post request
        connect.setDoOutput(true);

        DataOutputStream output = new DataOutputStream(connect.getOutputStream());
        output.writeBytes(data);
        output.flush();
        output.close();


        // getting the response information
        System.out.println("Response code : " + connect.getResponseCode());
        System.out.println("Response message : " + connect.getResponseMessage());

        // getting the result
        BufferedReader input = new BufferedReader(new InputStreamReader(connect.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = input.readLine()) != null){
            response.append(inputLine);
        }
        input.close();
        System.out.println(response.toString());**/


        /**ProcessBuilder processBuilder = new ProcessBuilder("curl", "'http://cloud.science-miner.com/nerd/service/disambiguate' -X POST -F \"query={ 'text': 'Austria invaded and fought the Serbian army at the Battle of Cer and Battle of Kolubara beginning on 12 August. \\n\\nThe army, led by general Paul von Hindenburg defeated Russia in a series of battles collectively known as the First Battle of Tannenberg (17 August – 2 September). But the failed Russian invasion, causing the fresh German troops to move to the east, allowed the tactical Allied victory at the First Battle of the Marne. \\n\\nUnfortunately for the Allies, the pro-German King Constantine I dismissed the pro-Allied government of E. Venizelos before the Allied expeditionary force could arrive. Beginning in 1915, the Italians under Cadorna mounted eleven offensives on the Isonzo front along the Isonzo River, northeast of Trieste.\\n\\n At the Siege of Maubeuge about 40000 French soldiers surrendered, at the battle of Galicia Russians took about 100-120000 Austrian captives, at the Brusilov Offensive about 325 000 to 417 000 Germans and Austrians surrendered to Russians, at the Battle of Tannenberg 92,000 Russians surrendered.\\n\\n After marching through Belgium, Luxembourg and the Ardennes, the German Army advanced, in the latter half of August, into northern France where they met both the French army, under Joseph Joffre, and the initial six divisions of the British Expeditionary Force, under Sir John French. A series of engagements known as the Battle of the Frontiers ensued. Key battles included the Battle of Charleroi and the Battle of Mons. In the former battle the French 5th Army was almost destroyed by the German 2nd and 3rd Armies and the latter delayed the German advance by a day.', 'processSentence': [ 1 ], 'sentences': [ { 'offsetStart': 0, 'offsetEnd': 138 }, { 'offsetStart': 138, 'offsetEnd': 293 } ], 'entities': [ ] }\"");
        Process process = processBuilder.start();

        InputStream inputStream = process.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();

        String line = new String();
        while ((line=bufferedReader.readLine()) == null){
            System.out.println("curl line : " + line);
            stringBuilder.append(line);
        }

        System.out.println(stringBuilder.toString());**/

        String output = "";
        String command = "curl 'http://cloud.science-miner.com/nerd/service/disambiguate' -X POST -F \"query={ 'text': 'Austria invaded and fought the Serbian army at the Battle of Cer and Battle of Kolubara beginning on 12 August. \\n\\nThe army, led by general Paul von Hindenburg defeated Russia in a series of battles collectively known as the First Battle of Tannenberg (17 August – 2 September). But the failed Russian invasion, causing the fresh German troops to move to the east, allowed the tactical Allied victory at the First Battle of the Marne. \\n\\nUnfortunately for the Allies, the pro-German King Constantine I dismissed the pro-Allied government of E. Venizelos before the Allied expeditionary force could arrive. Beginning in 1915, the Italians under Cadorna mounted eleven offensives on the Isonzo front along the Isonzo River, northeast of Trieste.\\n\\n At the Siege of Maubeuge about 40000 French soldiers surrendered, at the battle of Galicia Russians took about 100-120000 Austrian captives, at the Brusilov Offensive about 325 000 to 417 000 Germans and Austrians surrendered to Russians, at the Battle of Tannenberg 92,000 Russians surrendered.\\n\\n After marching through Belgium, Luxembourg and the Ardennes, the German Army advanced, in the latter half of August, into northern France where they met both the French army, under Joseph Joffre, and the initial six divisions of the British Expeditionary Force, under Sir John French. A series of engagements known as the Battle of the Frontiers ensued. Key battles included the Battle of Charleroi and the Battle of Mons. In the former battle the French 5th Army was almost destroyed by the German 2nd and 3rd Armies and the latter delayed the German advance by a day.', 'processSentence': [ 1 ], 'sentences': [ { 'offsetStart': 0, 'offsetEnd': 138 }, { 'offsetStart': 138, 'offsetEnd': 293 } ], 'entities': [ ] }\"";
        Process upload = Runtime.getRuntime().exec(command);
        upload.waitFor();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(upload.getInputStream()));
        String line = "";
        while ((line = bufferedReader.readLine()) != null){
            output = output.concat(line);
        }
        System.out.println(output);

    }

}
