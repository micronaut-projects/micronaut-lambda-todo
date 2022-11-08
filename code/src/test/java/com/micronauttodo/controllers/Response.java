package com.micronauttodo.controllers;

public class Response<T> {
    private final int status;
    private final T body;

    public Response(int status) {
        this(status, null);
    }

    public Response(int status, T body) {
        this.status = status;
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public T getBody() {
        return body;
    }
}
