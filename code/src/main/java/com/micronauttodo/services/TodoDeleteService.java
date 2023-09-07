package com.micronauttodo.services;

import com.micronauttodo.models.OAuthUser;
import io.micronaut.core.annotation.NonNull;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@FunctionalInterface
public interface TodoDeleteService {
    void delete(@NonNull @NotBlank String id,
                @NonNull @NotNull @Valid OAuthUser user);
}
