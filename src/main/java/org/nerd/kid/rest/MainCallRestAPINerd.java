package org.nerd.kid.rest;

import java.util.*;

public class MainCallRestAPINerd {
    public static void main(String[] args) throws Exception {

        // variables
        String url, query;

        // object of callRestAPI
        CallRestAPINERD callAPIINERD = new CallRestAPINERD();

        Scanner scanner = new Scanner(System.in);

        System.out.print("URL : ");
        url = scanner.nextLine();
        System.out.print("Query : ");
        query = scanner.nextLine();
        callAPIINERD.useCurl(url, query);
    }
}

