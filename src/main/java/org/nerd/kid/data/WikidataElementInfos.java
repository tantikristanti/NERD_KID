package org.nerd.kid.data;

import java.util.List;

public class WikidataElementInfos {
    private String wikidataId, label, realClass, predictedClass;

    private Double[] featureVector;

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

    public Double[] getFeatureVector() {
        return featureVector;
    }

    public void setFeatureVector(Double[] featureVector) {
        this.featureVector = featureVector;
    }

}
