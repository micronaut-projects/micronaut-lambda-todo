package com.micronauttodo.controllers;

import io.micronaut.aws.apigateway.StageResolver;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.context.ServerContextPathProvider;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.http.uri.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractController.class);
    private final HttpHostResolver httpHostResolver;

    private final StageResolver<HttpRequest<?>> stageResolver;
    private final ServerContextPathProvider serverContextPathProvider;

    protected AbstractController(HttpHostResolver httpHostResolver,
                                 StageResolver<HttpRequest<?>> stageResolver,
                                 ServerContextPathProvider serverContextPathProvider) {
        this.httpHostResolver = httpHostResolver;
        this.stageResolver = stageResolver;
        this.serverContextPathProvider = serverContextPathProvider;
    }

    protected MutableHttpResponse<?> seeOther(HttpRequest<?> request, Consumer<UriBuilder> uriBuilderConsumer) {
        UriBuilder builder = UriBuilder.of(httpHostResolver.resolve(request));
        Optional<String> stageOptional = stageResolver.resolve(request);
        if (stageOptional.isPresent()) {
            builder = builder.path(stageOptional.get());
        }
        if (StringUtils.isNotEmpty(serverContextPathProvider.getContextPath())) {
            builder = builder.path(serverContextPathProvider.getContextPath());
        }
        if (StringUtils.isNotEmpty(serverContextPathProvider.getContextPath())) {
            builder = builder.path(serverContextPathProvider.getContextPath());
        }
        uriBuilderConsumer.accept(builder);
        URI uri = builder.build();
        LOG.info("redirecting to {}", uri);
        return HttpResponse.seeOther(uri);
    }
}
