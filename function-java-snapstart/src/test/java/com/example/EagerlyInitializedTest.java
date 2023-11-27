package com.example;

import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertTrue;
class EagerlyInitializedTest {

    @Test
    void singletonsAreEagerlyInitialized() throws InterruptedException, IOException {
        try (ApiGatewayProxyRequestEventFunction handler = new ApiGatewayProxyRequestEventFunction()){
            sleep(5_000);
            assertTrue(handler.getApplicationContext().getBean(Clock.class).getNow().isBefore(LocalDateTime.now().minusSeconds(3)));
        }
    }
}