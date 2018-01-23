package org.nerd.kid.web.healthcheck;

import com.codahale.metrics.health.HealthCheck;

public class KidHealthCheck extends HealthCheck {

    public KidHealthCheck() {

    }

    @Override
    protected Result check() throws Exception {
        
        return Result.healthy();
    }
}