package org.nerd.kid.service;

public class NerdEntity {
    private String rawName =  null;
    private String normalisedRawName = null;
    private String typeNEGrobidNER = null;
    private String typeNENerdKid = null;
    private int offsetStart = -1;
    private int offsetEnd = -1;
    private double nerdScore = 0.0;
    private double selectionScore = 0.0;

    private double confidenceScore = 0.0;
    private int wikipediaExternalRef = -1;
    private String wikidataId = null;

    public String getRawName() {
        return rawName;
    }

    public void setRawName(String rawName) {
        this.rawName = rawName;
    }

    public String getNormalisedRawName() {
        return normalisedRawName;
    }

    public void setNormalisedRawName(String normalisedRawName) {
        this.normalisedRawName = normalisedRawName;
    }


    public String getTypeNEGrobidNER() {
        return typeNEGrobidNER;
    }

    public void setTypeNEGrobidNER(String typeNEGrobidNER) {
        this.typeNEGrobidNER = typeNEGrobidNER;
    }

    public String getTypeNENerdKid() {
        return typeNENerdKid;
    }

    public void setTypeNENerdKid(String typeNENerdKid) {
        this.typeNENerdKid = typeNENerdKid;
    }

    public int getOffsetStart() {
        return offsetStart;
    }

    public void setOffsetStart(int offsetStart) {
        this.offsetStart = offsetStart;
    }

    public int getOffsetEnd() {
        return offsetEnd;
    }

    public void setOffsetEnd(int offsetEnd) {
        this.offsetEnd = offsetEnd;
    }

    public double getNerdScore() {
        return nerdScore;
    }

    public void setNerdScore(double nerdScore) {
        this.nerdScore = nerdScore;
    }

    public double getSelectionScore() {
        return selectionScore;
    }

    public void setSelectionScore(double selectionScore) {
        this.selectionScore = selectionScore;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public int getWikipediaExternalRef() {
        return wikipediaExternalRef;
    }

    public void setWikipediaExternalRef(int wikipediaExternalRef) {
        this.wikipediaExternalRef = wikipediaExternalRef;
    }

    public String getWikidataId() {
        return wikidataId;
    }

    public void setWikidataId(String wikidataId) {
        this.wikidataId = wikidataId;
    }

}
