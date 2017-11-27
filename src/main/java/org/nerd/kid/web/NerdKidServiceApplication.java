package org.nerd.kid.web;


import com.google.common.collect.Lists;
import com.google.inject.Module;
import com.hubspot.dropwizard.guicier.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.nerd.kid.web.healthcheck.KidHealthCheck;
import org.nerd.kid.web.module.NerdKidServiceModule;
import org.nerd.kid.web.resource.KidPredictionResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class NerdKidServiceApplication extends Application<NerdKidConfiguration> {
    private static final String RESOURCES = "/service";


    // ========== Application ==========
    @Override
    public String getName() {
        return "nerd-kid-service";
    }

    @Override
    public void run(NerdKidConfiguration nerdKidConfiguration, Environment environment) throws Exception {
        environment.jersey().setUrlPattern(RESOURCES + "/*");

        final KidHealthCheck healthCheck = new KidHealthCheck();
        environment.healthChecks().register("kidHealth", healthCheck);
        environment.jersey().register(new KidPredictionResource());
    }

    private List<? extends Module> getGuiceModules() {
        return Lists.newArrayList(new NerdKidServiceModule());
    }


    @Override
    public void initialize(Bootstrap<NerdKidConfiguration> bootstrap) {
        GuiceBundle<NerdKidConfiguration> guiceBundle = GuiceBundle.defaultBuilder(NerdKidConfiguration.class)
                .modules(getGuiceModules())
                .build();
        bootstrap.addBundle(guiceBundle);
        bootstrap.addBundle(new MultiPartBundle());
    }

    // ========== static ==========
    public static void main(String... args) throws Exception {
        new NerdKidServiceApplication().run(args);
    }
}
