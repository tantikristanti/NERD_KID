package org.nerd.kid.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WikidataElement {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String label;

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

    private Map<String, List<String>> properties = new HashMap<>();
}
