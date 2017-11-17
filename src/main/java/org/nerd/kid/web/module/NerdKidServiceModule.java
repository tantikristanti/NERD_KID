package org.nerd.kid.web.module;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.hubspot.dropwizard.guicier.DropwizardAwareModule;
import org.nerd.kid.web.NerdKidConfiguration;
import org.nerd.kid.web.resource.KidPredictionResource;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class NerdKidServiceModule extends DropwizardAwareModule<NerdKidConfiguration> {


    @Override
    public void configure(Binder binder) {
        //REST
        binder.bind(KidPredictionResource.class);
    }

    @Provides
    protected ObjectMapper getObjectMapper() {
        return getEnvironment().getObjectMapper();
    }

    @Provides
    protected MetricRegistry provideMetricRegistry() {
        return getMetricRegistry();
    }

    //for unit tests
    protected MetricRegistry getMetricRegistry() {
        return getEnvironment().metrics();
    }

    @Provides
    Client provideClient() {
        return ClientBuilder.newClient();
    }

}
