package rest;

//import com.scienceminer.nerd.utilities.NerdProperties;

import java.util.Scanner;

public class mainRestService {
    public static void main(String[] args) throws Exception {

        // variables
        String url, query;

        // object of callRestAPI
        callRestAPINERD callAPIINERD = new callRestAPINERD();
        callAPIWikidata callWiki = new callAPIWikidata();

        Scanner scanner = new Scanner(System.in);

        /**System.out.print("URL : ");
        url = scanner.nextLine();
        System.out.print("Query : ");
        query = scanner.nextLine();
        callAPIINERD.useCurl(url, query);**/
        callAPIINERD.readJSON();
        callWiki.getPropertyFromId();


    }
}
