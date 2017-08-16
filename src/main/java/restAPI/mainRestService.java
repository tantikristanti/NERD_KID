package restAPI;

//import com.scienceminer.nerd.utilities.NerdProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Scanner;

public class mainRestService {
    public static void main(String[] args) throws Exception {

        BufferedReader br = null;
        String url, query;

        System.out.println("Accessing REST API Nerd");
        //NerdProperties.getInstance();
        callRestAPI call = new callRestAPI();

        Scanner scanner = new Scanner(System.in);

        System.out.print("Url: ");
        url = scanner.nextLine();

        System.out.print("Query: ");
        query = scanner.nextLine();

        call.callRestService(url,query);
    }
}
