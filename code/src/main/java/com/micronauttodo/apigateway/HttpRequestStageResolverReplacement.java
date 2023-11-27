package com.micronauttodo.apigateway;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import io.micronaut.aws.apigateway.HttpRequestStageResolver;
import io.micronaut.aws.apigateway.StageResolver;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import jakarta.inject.Singleton;
import io.micronaut.servlet.http.ServletHttpRequest;
import java.util.Optional;

// remove when there is aws version with this https://github.com/micronaut-projects/micronaut-aws/pull/1963
@Replaces(HttpRequestStageResolver.class)
@Singleton
public class HttpRequestStageResolverReplacement implements StageResolver<HttpRequest<?>> {
    @Override
    @NonNull
    public Optional<String> resolve(@NonNull HttpRequest<?> request) {
        if (!(request instanceof ServletHttpRequest servletHttpRequest)) {
            return Optional.empty();
        }
        Object nativeRequest = servletHttpRequest.getNativeRequest();
        if (nativeRequest instanceof APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent && apiGatewayProxyRequestEvent.getRequestContext() != null) {
            return Optional.of(apiGatewayProxyRequestEvent.getRequestContext().getStage());
        }
        return Optional.empty();
    }
}
