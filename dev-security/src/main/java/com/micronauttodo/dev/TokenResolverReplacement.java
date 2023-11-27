package com.micronauttodo.dev;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.token.generator.TokenGenerator;
import io.micronaut.security.token.reader.TokenResolver;
import jakarta.inject.Singleton;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Requires(beans = DevAuthenticationFetcher.class)
@Replaces(TokenResolver.class)
@Singleton
public class TokenResolverReplacement implements TokenResolver<HttpRequest<?>> {
    private final TokenGenerator tokenGenerator;
    private final DevAuthenticationFetcher devAuthenticationFetcher;

    public TokenResolverReplacement(TokenGenerator tokenGenerator,
                                    DevAuthenticationFetcher devAuthenticationFetcher) {
        this.tokenGenerator = tokenGenerator;
        this.devAuthenticationFetcher = devAuthenticationFetcher;
    }


    @Override
    @Deprecated
    public Optional<String> resolveToken(HttpRequest<?> request) {
        return tokenGenerator.generateToken(devAuthenticationFetcher.claims());
    }

    @Override
    public List<String> resolveTokens(HttpRequest<?> request) {
        return tokenGenerator.generateToken(devAuthenticationFetcher.claims())
                .map(Collections::singletonList)
                .orElseGet(Collections::emptyList);
    }
}
