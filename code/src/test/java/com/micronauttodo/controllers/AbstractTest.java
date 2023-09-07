package com.micronauttodo.controllers;

import com.amazonaws.services.lambda.runtime.Context;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction;
import io.micronaut.http.HttpRequest;
import io.micronaut.json.JsonMapper;
import org.junit.jupiter.api.AfterAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTest {
    private static ApiGatewayProxyRequestEventFunction handler;
    private static Context lambdaContext;

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
        handler = new ApiGatewayProxyRequestEventFunction(ApplicationContext.builder().properties(getProperties(properties)).build());
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

    protected Response<?> exchange(HttpRequest<?> request) throws IOException {
        return exchange(request, null);
    }

    protected <T> Response<T> exchange(HttpRequest<?> request, @Nullable Class<T> responseType) throws IOException {
        return TestUtils.exchange(handler, getBean(JsonMapper.class), lambdaContext, request, responseType);
    }
}
