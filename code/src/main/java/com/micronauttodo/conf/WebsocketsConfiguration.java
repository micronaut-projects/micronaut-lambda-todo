package com.micronauttodo.conf;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Requires(property = "websockets.url")
@ConfigurationProperties("websockets")
public interface WebsocketsConfiguration {
    @NotBlank
    @NonNull
    @Pattern(regexp = "[wss://|ws://].*")
    String getUrl();
}
