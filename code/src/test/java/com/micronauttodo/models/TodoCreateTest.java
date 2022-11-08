package com.micronauttodo.models;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TodoCreateTest {
    @Test
    void todoIsAnnotatedWithSerdeableDeserializable() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(TodoCreate.class));
    }
}