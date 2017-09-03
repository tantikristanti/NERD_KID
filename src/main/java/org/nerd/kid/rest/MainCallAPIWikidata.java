package org.nerd.kid.rest;

public class MainCallAPIWikidata {
    public static void main(String[] args) throws Exception {
        System.out.println("This process takes several minutes.");
        CallAPIWikidata callWiki = new CallAPIWikidata();
        callWiki.appendNewTestData();
    }
}