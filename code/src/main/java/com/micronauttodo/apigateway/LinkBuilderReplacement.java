package com.micronauttodo.apigateway;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.server.HttpServerConfiguration;
import io.micronaut.views.thymeleaf.LinkBuilder;
import io.micronaut.views.thymeleaf.WebEngineContext;
import jakarta.inject.Singleton;
import org.thymeleaf.context.IExpressionContext;
import  io.micronaut.aws.apigateway.StageResolver;
import java.util.Map;

@Singleton
@Replaces(LinkBuilder.class)
public class LinkBuilderReplacement extends LinkBuilder {
    private static final String SLASH = "/";
    private final StageResolver<HttpRequest<?>> stageResolver;

    public LinkBuilderReplacement(HttpServerConfiguration httpServerConfiguration,
                                  StageResolver<HttpRequest<?>> stageResolver) {
        super(httpServerConfiguration);
        this.stageResolver = stageResolver;
    }

    @Override
    protected String computeContextPath(IExpressionContext context, String base, Map<String, Object> parameters) {
        String contextPath = super.computeContextPath(context, base, parameters);
        if (context instanceof WebEngineContext webEngineContext) {
            contextPath = stageResolver.resolve(webEngineContext.getRequest())
                    .map(stage -> stage.startsWith(SLASH) ? stage : SLASH + stage)
                    .orElse(contextPath);
        }
        return contextPath;
    }
}
