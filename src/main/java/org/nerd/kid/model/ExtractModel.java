package org.nerd.kid.model;


import java.io.File;

public class ExtractModel {
    public static void main(String[] args) {
        ModelBuilder modelBuilder = new ModelBuilder();
        String pathModelZip = "src/main/resources/model.zip";
        String pathModelExtracted = "src/main/resources/model.xml";

        try{
            modelBuilder.extractZip(new File(pathModelZip),new File(pathModelExtracted));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            System.out.println("Decompressing the Zip file ...");
        }
    }
}
