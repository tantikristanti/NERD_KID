package restAPI;

//import com.scienceminer.nerd.utilities.NerdProperties;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

import java.util.List;
import java.util.Scanner;

public class mainRestService {
    public static void main(String[] args) throws Exception {

        // variables
        String url, query;

        // object of callRestAPI
        callRestAPINERD callAPIINERD = new callRestAPINERD();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Accessing REST API of NERD, Example in /data/example/exampleCurlNERD.txt");
        /**System.out.print("URL : ");
        url = scanner.nextLine();
        System.out.print("Query : ");
        query = scanner.nextLine();
        call.useCurl(url, query);**/
        //call.readJSON();


    }
}
