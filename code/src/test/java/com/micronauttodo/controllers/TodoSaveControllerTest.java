package com.micronauttodo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.Todo;
import com.micronauttodo.repositories.TodoRepository;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers(disabledWithoutDocker = true)
class TodoSaveControllerTest extends AbstractTest {

    @BeforeAll
    public static void setupSpec() {
        startHandler(CollectionUtils.mapOf("micronaut.security.filter.enabled", StringUtils.FALSE,
                "micronaut.http.client.follow-redirects", StringUtils.FALSE));
    }

    private static final OAuthUser OAUTHUSER = new OAuthUser("https://cognito-idp.us-east-1.amazonaws.com/us-east-1_abcdemu6o0#",
            "014e7c43-ff5c-23e7-4506-124fe64d2303",
            "john@email.com");


    @Test
    void testTodoSave() throws JsonProcessingException {
        ObjectMapper objectMapper = getBean(ObjectMapper.class);
        TodoRepository todoRepository = getBean(TodoRepository.class);
        String task = "Clean";
        HttpRequest<?> request = HttpRequest.POST("/todo", Collections.singletonMap("task", task))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
        Response<?> response = exchange(request);
        assertEquals(HttpStatus.SEE_OTHER.getCode(), response.getStatus());
        List<Todo> todoList = todoRepository.findAll(OAUTHUSER);
        assertEquals(1, todoList.size());
        assertEquals(task, todoList.get(0).getTask());
        todoRepository.delete(todoList.get(0).getId(), OAUTHUSER);
        assertEquals(0, todoRepository.findAll(OAUTHUSER).size());
    }


    @Replaces(OauthUserArgumentBinder.class)
    @Singleton
    static class OauthUserArgumentBinderReplacement extends OauthUserArgumentBinder {
        @Override
        public BindingResult<OAuthUser> bind(ArgumentConversionContext<OAuthUser> context, HttpRequest<?> source) {
            return () -> Optional.of(OAUTHUSER);
        }
    }
}