package com.micronauttodo.models.events;

import com.micronauttodo.models.OAuthUser;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Introspected
public class TodoDeletedEvent {
    @NonNull
    @NotBlank
    private final String id;

    @NonNull
    @NotNull
    @Valid
    private final OAuthUser user;

    public TodoDeletedEvent(@NonNull String id,
                            @NonNull OAuthUser oAuthUser) {
        this.id = id;
        this.user = oAuthUser;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public OAuthUser getUser() {
        return user;
    }
}
