package com.micronauttodo.controllers;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.function.aws.proxy.MicronautLambdaHandler;
import io.micronaut.http.HttpRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTest {
    private static MicronautLambdaHandler handler;
    private static Context lambdaContext = new MockLambdaContext();

    @Container
    static GenericContainer dynamoDBLocal =
            new GenericContainer("amazon/dynamodb-local")
                    .withExposedPorts(8000);

    @NonNull
    public static Map<String, Object> getProperties(Map<String, Object> properties) {
        if (!dynamoDBLocal.isRunning()) {
            dynamoDBLocal.start();
        }
        Map<String, Object> m = new HashMap<>();
        m.putAll(CollectionUtils.mapOf(
                "dynamodb-local.host", "localhost",
                "dynamodb-local.port", dynamoDBLocal.getFirstMappedPort()));
        m.putAll(properties);
        return m;
    }

    protected static void startHandler(Map<String, Object> properties) {
        try {
            handler = new MicronautLambdaHandler(ApplicationContext.builder().properties(getProperties(properties)));
        } catch (ContainerInitializationException e) {
            e.printStackTrace();
        }
    }

    protected static void startHandler() {
        startHandler(Collections.emptyMap());
    }

    @AfterAll
    public static void cleanupSpec() {
        handler.getApplicationContext().close();
    }

    protected <T> T getBean(Class<T> beanType) {
        return handler.getApplicationContext().getBean(beanType);
    }

    protected Response<?> exchange(HttpRequest<?> request) throws JsonProcessingException {
        return exchange(request, null);
    }

    protected <T> Response<T> exchange(HttpRequest<?> request, @Nullable Class<T> responseType) throws JsonProcessingException {
        return TestUtils.exchange(getBean(ObjectMapper.class), handler, lambdaContext, request, responseType);
    }
}
