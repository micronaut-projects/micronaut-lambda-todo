package com.micronauttodo.conf;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Requires(property = "assets.url")
@ConfigurationProperties("assets")
public interface AssetsConfiguration {
    @NotBlank
    @Pattern(regexp = "https://.*")
    @NonNull
    String getUrl();
}
