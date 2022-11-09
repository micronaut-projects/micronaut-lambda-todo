package com.micronauttodo.controllers;

import com.micronauttodo.views.Model;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.Hidden;

@Controller
public class UnauthorizedController {
    private final HttpHostResolver httpHostResolver;

    public UnauthorizedController(HttpHostResolver httpHostResolver) {
        this.httpHostResolver = httpHostResolver;
    }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Hidden
    @View("unauthorized/index.html")
    @Produces(MediaType.TEXT_HTML)
    @Get("/unauthorized")
    Model index(HttpRequest<?> request) {
        return new Model(httpHostResolver.resolve(request));
    }
}
