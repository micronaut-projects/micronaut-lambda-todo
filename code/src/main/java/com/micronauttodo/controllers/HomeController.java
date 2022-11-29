package com.micronauttodo.controllers;

import com.micronauttodo.apigateway.StageResolver;
import com.micronauttodo.models.OAuthUser;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.context.ServerContextPathProvider;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.ModelAndView;
import io.swagger.v3.oas.annotations.Hidden;

import java.net.URISyntaxException;
import java.util.Collections;

@Controller
public class HomeController extends AbstractController {
    public HomeController(HttpHostResolver httpHostResolver,
                          StageResolver stageResolver,
                          ServerContextPathProvider serverContextPathProvider) {
        super(httpHostResolver, stageResolver, serverContextPathProvider);
    }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Hidden
    @Get
    HttpResponse<?> index(@Nullable OAuthUser oAuthUser, HttpRequest<?> request) throws URISyntaxException {
        if (oAuthUser != null) {
            return seeOther(request, uriBuilder -> uriBuilder.path("todo"));
        }
        return HttpResponse.ok(new ModelAndView<>("home/index.html", Collections.emptyMap()));
    }
}