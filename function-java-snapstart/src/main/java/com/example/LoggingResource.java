package com.example;

import io.micronaut.crac.OrderedResource;
import jakarta.inject.Singleton;
import org.crac.Context;
import org.crac.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class LoggingResource implements OrderedResource {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingResource.class);
    @Override
    public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
        LOG.info("before creating a snapshot");
    }

    @Override
    public void afterRestore(Context<? extends Resource> context) throws Exception {
        LOG.info("restoring the snapshot");
    }
}
