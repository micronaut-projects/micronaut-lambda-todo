package com.micronauttodo.apigateway;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import jakarta.inject.Singleton;

import java.util.Optional;

@Singleton
public class DefaultStageResolver implements StageResolver {

    @Override
    @NonNull
    public Optional<String> resolveStage(@Nullable HttpRequest<?> request) {
        // TODO: What is this??
        return Optional.empty();
    }
}
