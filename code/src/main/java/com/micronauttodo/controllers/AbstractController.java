package com.micronauttodo.controllers;

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
import java.util.function.Consumer;

public abstract class AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractController.class);
    private final HttpHostResolver httpHostResolver;
    private final ServerContextPathProvider serverContextPathProvider;

    protected AbstractController(HttpHostResolver httpHostResolver,
                                 ServerContextPathProvider serverContextPathProvider) {
        this.httpHostResolver = httpHostResolver;
        this.serverContextPathProvider = serverContextPathProvider;
    }

    protected MutableHttpResponse<?> seeOther(HttpRequest<?> request, Consumer<UriBuilder> uriBuilderConsumer) {
        UriBuilder builder = UriBuilder.of(httpHostResolver.resolve(request));
        if (StringUtils.isNotEmpty(serverContextPathProvider.getContextPath())) {
            builder = builder.path(serverContextPathProvider.getContextPath());
        }
        uriBuilderConsumer.accept(builder);
        URI uri = builder.build();
        LOG.info("redirecting to {}", uri);
        return HttpResponse.seeOther(uri);
    }
}
