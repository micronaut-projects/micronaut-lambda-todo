package com.micronauttodo.controllers;

import io.micronaut.core.annotation.Internal;

@Internal
public class Message {
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
