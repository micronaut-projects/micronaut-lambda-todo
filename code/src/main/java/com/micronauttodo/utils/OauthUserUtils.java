package com.micronauttodo.utils;

import com.micronauttodo.models.OAuthUser;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.security.authentication.Authentication;

import java.util.Optional;

import static io.micronaut.security.token.Claims.ISSUER;
import static io.micronaut.security.token.Claims.SUBJECT;

public final class OauthUserUtils {

    public static final String CLAIM_EMAIL = "email";

    private OauthUserUtils() {
    }

    @NonNull
    public static Optional<OAuthUser> parseOAuthUser(@Nullable Authentication authentication) {
        return authentication != null ?
                Optional.of(OauthUserUtils.toOauthUser(authentication)) :
                Optional.empty();
    }

    @NonNull
    public static OAuthUser toOauthUser(@NonNull Authentication authentication) {
        return new OAuthUser(authentication.getAttributes().get(ISSUER).toString(),
                authentication.getAttributes().get(SUBJECT).toString(),
                authentication.getAttributes().get(CLAIM_EMAIL).toString());
    }
}
