package com.micronauttodo.repositories.dynamodb;

import com.amazonaws.xray.interceptors.TracingInterceptor;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

@Requires(classes = TracingInterceptor.class)
@Requires(Environment.AMAZON_EC2)
@Singleton
class TracingInterceptorDynamoDbClientBuilderListener
        implements BeanCreatedEventListener<DynamoDbClientBuilder> {

    @Override
    public DynamoDbClientBuilder onCreated(BeanCreatedEvent<DynamoDbClientBuilder> event) {
        DynamoDbClientBuilder builder = event.getBean();
        builder.overrideConfiguration(b -> b.addExecutionInterceptor(new TracingInterceptor()));
        return builder;
    }
}
