package com.example;

import com.micronauttodo.models.TodoCreate;
import com.micronauttodo.repositories.TodoRepository;
import com.micronauttodo.services.TodoSaveService;
import com.micronauttodo.utils.OauthUserUtils;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.crac.OrderedResource;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.jwt.generator.AccessTokenConfiguration;
import io.micronaut.security.token.jwt.generator.claims.ClaimsGenerator;
import io.micronaut.security.token.jwt.generator.claims.JwtClaims;
import jakarta.inject.Singleton;
import org.crac.Context;
import org.crac.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;

@Singleton
public class PrimingResource implements OrderedResource {
    private static final Logger LOG = LoggerFactory.getLogger(PrimingResource.class);
    private static final int ITERATIONS = 1;
    private static final String ID = "014e7c43-ff5c-23e7-4506-124fe64d2303";

    private final AccessTokenConfiguration accessTokenConfiguration;
    private final ClaimsGenerator claimsGenerator;

    private final TodoSaveService todoSaveService;
    private final TodoRepository todoRepository;

    public PrimingResource(AccessTokenConfiguration accessTokenConfiguration,
                           ClaimsGenerator claimsGenerator,
                           TodoSaveService todoSaveService,
                           TodoRepository todoRepository) {
        this.accessTokenConfiguration = accessTokenConfiguration;
        this.claimsGenerator = claimsGenerator;
        this.todoSaveService = todoSaveService;
        this.todoRepository = todoRepository;
    }

    @Override
    public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
        LOG.info("priming...");
        OauthUserUtils.parseOAuthUser(authentication()).ifPresent(user -> {
            for (int i = 0; i < ITERATIONS; i++) {
                String todoId = todoSaveService.save(new TodoCreate(randomAlphanumeric(20)), user);
                todoRepository.delete(todoId, user);
            }
        });
        LOG.info("finished priming");
    }

    @Override
    public void afterRestore(Context<? extends Resource> context) throws Exception {

    }

    @NonNull
    private Authentication authentication() {
        return new Authentication() {
            @Override
            @NonNull
            public Map<String, Object> getAttributes() {
                return claims();
            }
            @Override
            public String getName() {
                return ID;
            }
        };
    }

    private Map<String, Object> claims() {
        return claimsGenerator.generateClaimsSet(oldClaims(), accessTokenConfiguration.getExpiration());
    }

    private static Map<String, Object> oldClaims() {
        return Map.of(
                JwtClaims.ISSUER, "https://cognito-idp.us-east-1.amazonaws.com/us-east-1_abcdemu6o0#",
                JwtClaims.SUBJECT, ID,
                "email", "john@email.com"
        );
    }


    private static String randomAlphanumeric(int length) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }
}
