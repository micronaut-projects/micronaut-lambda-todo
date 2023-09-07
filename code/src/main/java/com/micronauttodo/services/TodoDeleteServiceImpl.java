package com.micronauttodo.services;

import com.micronauttodo.models.events.TodoDeletedEvent;
import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.repositories.dynamodb.TodoRepository;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Requires(beans = {TodoRepository.class})
@Singleton
public class TodoDeleteServiceImpl implements TodoDeleteService {

    private final ApplicationEventPublisher<TodoDeletedEvent> applicationEventPublisher;
    private final TodoRepository todoRepository;

    public TodoDeleteServiceImpl(ApplicationEventPublisher<TodoDeletedEvent> applicationEventPublisher,
                                 TodoRepository todoRepository) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.todoRepository = todoRepository;
    }

    @Override
    public void delete(@NonNull @NotBlank String id,
                       @NonNull @NotNull @Valid OAuthUser user) {
        todoRepository.delete(id, user);
        applicationEventPublisher.publishEvent(new TodoDeletedEvent(id, user));
    }
}
