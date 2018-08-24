package org.nerd.kid.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
Wikidata Element class is made to collect the Wikidata Id, Label, and the properties of this Wikidata Id
* */

public class WikidataElement {
    private String id;

    private String label;

    private Map<String, List<String>> properties = new HashMap<>();

    private List<String> propertiesNoValue;

    public List<String> getPropertiesNoValue() {
        return propertiesNoValue;
    }

    public void setPropertiesNoValue(List<String> propertiesNoValue) {
        this.propertiesNoValue = propertiesNoValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Map<String, List<String>> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, List<String>> properties) {
        this.properties = properties;
    }

}
