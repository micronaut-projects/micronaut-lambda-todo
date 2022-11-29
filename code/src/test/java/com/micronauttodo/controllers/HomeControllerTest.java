package com.micronauttodo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HomeControllerTest extends AbstractTest {

    @BeforeAll
    public static void setupSpec() {
        startHandler(Collections.emptyMap());
    }

    @Test
    void testHome() throws JsonProcessingException {
        ObjectMapper objectMapper = getBean(ObjectMapper.class);
        HttpRequest<?> request = HttpRequest.GET("/");
        Response<Message> response = exchange(request, Message.class);
        assertEquals(HttpStatus.OK.getCode(), response.getStatus());
        Message body = response.getBody();
        assertEquals(body.getMessage(), "Hello World");
    }
}