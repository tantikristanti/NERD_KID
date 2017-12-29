package org.nerd.kid.data;

import java.util.List;

public class WikidataElementInfos {
    public String getWikidataId() {
        return wikidataId;
    }

    public void setWikidataId(String wikidataId) {
        this.wikidataId = wikidataId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getRealClass() {
        return realClass;
    }

    public void setRealClass(String realClass) {
        this.realClass = realClass;
    }

    public String getPredictedClass() {
        return predictedClass;
    }

    public void setPredictedClass(String predictedClass) {
        this.predictedClass = predictedClass;
    }

    public Integer[] getFeatureVector() {
        return featureVector;
    }

    public void setFeatureVector(Integer[] featureVector) {
        this.featureVector = featureVector;
    }

    private String wikidataId, label, realClass, predictedClass;
    
    private Integer[] featureVector;

    public List<String> getRawFeatureVector() {
        return rawFeatureVector;
    }

    public void setRawFeatureVector(List<String> rawFeatureVector) {
        this.rawFeatureVector = rawFeatureVector;
    }

    private List<String> rawFeatureVector;

}
