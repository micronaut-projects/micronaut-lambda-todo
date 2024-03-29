package com.micronauttodo.repositories.dynamodb;

import com.micronauttodo.repositories.dynamodb.constants.DynamoConfiguration;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;

import jakarta.validation.constraints.NotBlank;

@Requires(property = "dynamodb.table-name")
@ConfigurationProperties("dynamodb")
public class DynamoConfigurationProperties implements DynamoConfiguration {
    @NotBlank
    private String tableName;

    @Override
    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
