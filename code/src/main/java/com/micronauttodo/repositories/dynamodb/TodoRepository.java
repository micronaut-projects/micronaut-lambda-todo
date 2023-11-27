package com.micronauttodo.repositories.dynamodb;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.Todo;
import io.micronaut.core.annotation.NonNull;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface TodoRepository {

    void delete(@NonNull @NotBlank String id,
                @NonNull @NotNull @Valid OAuthUser user);

    void save(@NonNull @NotNull @Valid Todo todo,
              @NonNull @NotNull @Valid OAuthUser user);

    @NonNull
    List<Todo> findAll(@NonNull @NotNull @Valid OAuthUser oAuthUser);

    @NonNull
    Optional<Todo> findById(@NonNull @NotBlank String id,
                            @NonNull @NotNull @Valid OAuthUser user);
}
