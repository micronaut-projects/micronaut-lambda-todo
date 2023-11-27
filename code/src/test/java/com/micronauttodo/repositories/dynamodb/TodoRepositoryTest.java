package com.micronauttodo.repositories.dynamodb;

import com.micronauttodo.controllers.AbstractTest;
import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.TodoCreate;
import com.micronauttodo.services.TodoSaveService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers(disabledWithoutDocker = true)
class TodoRepositoryTest extends AbstractTest {

    @BeforeAll
    public static void setupSpec() {
        startHandler();
    }

    @Test
    void testTodoSave() {
        TodoSaveService todoSaveService = getBean(TodoSaveService.class);
        TodoRepository todoRepository = getBean(TodoRepository.class);

        OAuthUser oAuthUser = new OAuthUser("https://cognito-idp.us-east-1.amazonaws.com/us-east-1_abcdemu6o0#",
                "014e7c43-ff5c-23e7-4506-124fe64d2303",
                "john@email.com");
        String todoId = todoSaveService.save(new TodoCreate("Clean"), oAuthUser);
        assertNotNull(todoId);
        assertEquals(1, todoRepository.findAll(oAuthUser).size());
        todoRepository.delete(todoId, oAuthUser);
        assertEquals(0, todoRepository.findAll(oAuthUser).size());
    }
}