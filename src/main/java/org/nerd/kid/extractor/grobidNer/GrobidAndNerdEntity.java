package org.nerd.kid.extractor.grobidNer;

/*
This class contains objects as a collection of results of Grobid-Ner and Entity-Fishing projects
* */

import org.nerd.kid.service.NerdEntity;

import java.util.List;

public class GrobidAndNerdEntity {
    GrobidNerEntity grobidNerEntity;
    List<NerdEntity> entities;

    public GrobidNerEntity getGrobidNerEntity() {
        return grobidNerEntity;
    }

    public void setGrobidNerEntity(GrobidNerEntity grobidNerEntity) {
        this.grobidNerEntity = grobidNerEntity;
    }

    public List<NerdEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<NerdEntity> entities) {
        this.entities = entities;
    }
}
