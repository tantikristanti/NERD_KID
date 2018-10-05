package org.nerd.kid.web.resource;

import com.google.inject.Inject;
import org.nerd.kid.data.WikidataElementInfos;
import org.nerd.kid.extractor.wikidata.NerdKBFetcherWrapper;
import org.nerd.kid.extractor.wikidata.WikidataFetcherWrapper;
import org.nerd.kid.model.WikidataNERPredictor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

@Path("/ner")
public class KidPredictionResource {
    private WikidataNERPredictor predictor = null;
    WikidataFetcherWrapper wrapper = new NerdKBFetcherWrapper();

    @Inject
    public KidPredictionResource() {
        this.predictor = new WikidataNERPredictor(wrapper);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public WikidataElementInfos predictNERClass(@QueryParam("id") Optional<String> name) {
        return predictor.predict(name.orElseThrow(RuntimeException::new));
    }
}

