package restAPI;

//import com.scienceminer.nerd.utilities.NerdProperties;

import java.util.Scanner;

public class mainRestService {
    public static void main(String[] args) throws Exception {

        // variables
        String url, query;

        // object of callRestAPI
        callRestAPI call = new callRestAPI();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Accessing REST API of NERD, Example in /data/example/exampleCurl.txt");
        System.out.print("URL : ");
        url = scanner.nextLine();
        System.out.println("Query : ");
        query = scanner.nextLine();
        System.out.println(url +"  "+ query);
        call.useCurl(url, query);
    }
}
