package org.nerd.kid.arff;

import org.nerd.kid.extractor.ClassExtractor;
import org.nerd.kid.extractor.FeatureWikidataExtractor;

import java.util.List;
import java.util.Map;

/*
main class for generating Arff file

It is possible to change the list of features and the list of classes

The list of features can be found in 'data/resource/feature_mapper.csv'
The list of classes can be found in 'data/resource/class_mapper.csv'

* */

public class MainTrainerGenerator {

    public static void main(String[] args) throws Exception {
        ArffFileGenerator arffFileGenerator = new ArffFileGenerator();
        FeatureWikidataExtractor featureWikidataExtractor = new FeatureWikidataExtractor();
        ClassExtractor classExtractor = new ClassExtractor();

        // get the list of features
        Map<String, List<String>> resultFeature = featureWikidataExtractor.loadFeatures();

        // get the list classes
        List<String> resultClass = classExtractor.loadClasses();

        // generate new training file of Arff
        arffFileGenerator.createNewFile();
        arffFileGenerator.addHeader();
        arffFileGenerator.addAttribute(resultFeature);
        arffFileGenerator.addClassHeader(resultClass);
        arffFileGenerator.close();

        System.out.print("Result can be seen in 'result/arff/Training.arff' ");
    }
}
