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
import jakarta.annotation.security.PermitAll;

@PermitAll
@Controller
public class NotFoundController {

    private final HttpHostResolver httpHostResolver;

    public NotFoundController(HttpHostResolver httpHostResolver) {
        this.httpHostResolver = httpHostResolver;
    }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Hidden
    @Produces(MediaType.TEXT_HTML)
    @View("notFound/index.html")
    @Get("/404")
    Model index(HttpRequest<?> request) {
        return new Model(httpHostResolver.resolve(request));
    }
}
