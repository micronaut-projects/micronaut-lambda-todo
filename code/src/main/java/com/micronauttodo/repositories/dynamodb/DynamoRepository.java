package com.micronauttodo.repositories.dynamodb;

import com.micronauttodo.repositories.dynamodb.constants.DynamoConfiguration;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.beans.BeanProperty;
import io.micronaut.core.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.micronauttodo.repositories.dynamodb.constants.DynamoDbConstants.ATTRIBUTE_GSI_1_PK;
import static com.micronauttodo.repositories.dynamodb.constants.DynamoDbConstants.ATTRIBUTE_GSI_1_SK;
import static com.micronauttodo.repositories.dynamodb.constants.DynamoDbConstants.ATTRIBUTE_PK;
import static com.micronauttodo.repositories.dynamodb.constants.DynamoDbConstants.INDEX_GSI_1;

public abstract class DynamoRepository {
    private static final Logger LOG = LoggerFactory.getLogger(DynamoRepository.class);
    protected final DynamoDbClient dynamoDbClient;
    protected final DynamoConfiguration dynamoConfiguration;

    protected DynamoRepository(DynamoDbClient dynamoDbClient,
                            DynamoConfiguration dynamoConfiguration) {
        this.dynamoDbClient = dynamoDbClient;
        this.dynamoConfiguration = dynamoConfiguration;
    }

    protected void save(Map<String, AttributeValue> item) {
        PutItemResponse itemResponse = dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(dynamoConfiguration.getTableName())
                .item(item)
                .build());
        if (LOG.isDebugEnabled()) {
            LOG.debug("{}", itemResponse);
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("****************************");
            for (var k : item.entrySet()) {
                LOG.trace("{}: {}", k.getKey(), k.getValue().s());
            }
            LOG.trace("****************************");
        }
    }

    @NonNull
    protected <T> Optional<T> find(@NonNull Class<T> cls,
                                   @NonNull Map<String, AttributeValue> key) {
        GetItemResponse itemResponse = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(dynamoConfiguration.getTableName())
                .key(key)
                .build());
        return (itemResponse.hasItem()) ?
                Optional.of(fromItem(cls, itemResponse.item())) :
                Optional.empty();
    }

    protected QueryRequest findAllQueryRequest(@NonNull String pk,
                                               @NonNull String sk,
                                               @Nullable AttributeValue lastEvaluatedKey,
                                               @Nullable Integer limit) {
        return findAllQueryRequest(pk, sk, INDEX_GSI_1, ATTRIBUTE_GSI_1_PK, ATTRIBUTE_GSI_1_SK, lastEvaluatedKey, limit);
    }
    protected QueryRequest findAllQueryRequest(@NonNull String pk,
                                               @NonNull String sk,
                                               @NonNull String indexName,
                                               @NonNull String pkAttribute,
                                               @NonNull String skAttribute,
                                               @Nullable AttributeValue lastEvaluatedKey,
                                               @Nullable Integer limit) {
        QueryRequest.Builder builder = QueryRequest.builder()
                .tableName(dynamoConfiguration.getTableName())
                .indexName(indexName)
                .scanIndexForward(false);
        if (limit != null) {
            builder.limit(limit);
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("pk {} sk: {}", pk, sk);
        }
        return builder.keyConditionExpression(
                        lastEvaluatedKey == null ?
                                "#pk = :pk and begins_with(#sk,:sk)" :
                                "#pk = :pk and begins_with(#sk,:sk) and #sk < :lastKey")
                .expressionAttributeNames(Map.of("#pk", pkAttribute, "#sk", skAttribute))
                .expressionAttributeValues(lastEvaluatedKey == null ?
                        Map.of(":pk", s(pk), ":sk", s(sk)):
                        Map.of(":pk", s(pk), ":sk", s(sk), ":lastKey", lastEvaluatedKey))

                .build();
    }

    @NonNull
    private AttributeValue s(@NonNull String str) {
        return AttributeValue.builder().s(str).build();
    }

    protected void delete(Map<String, AttributeValue> key) {
        DeleteItemResponse deleteItemResponse = dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                .tableName(dynamoConfiguration.getTableName())
                .key(key)
                .build());
        if (LOG.isDebugEnabled()) {
            LOG.debug(deleteItemResponse.toString());
        }
    }

    protected Optional<Map<String, AttributeValue>> findByKey(@NonNull Map<String, AttributeValue> key) {
        GetItemResponse getItemResponse = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(dynamoConfiguration.getTableName())
                .key(key)
                .build());
        return !getItemResponse.hasItem() ? Optional.empty() : Optional.of(getItemResponse.item());
    }

    public <T> List<T> findAll(Class<T> cls,
                               @NonNull String gsi1Pk,
                               @NonNull String gsi1Sk) {
        List<T> result = new ArrayList<>();
        AttributeValue lastEvaluatedPk = null;
        do {
            QueryRequest request = findAllQueryRequest(gsi1Pk, gsi1Sk, lastEvaluatedPk, null);
            QueryResponse response = dynamoDbClient.query(request);
            if (LOG.isTraceEnabled()) {
                LOG.trace(response.toString());
            }
            result.addAll(fromResponse(cls, response));
            if (response.hasLastEvaluatedKey()) {
                Map<String, AttributeValue> lastEvaluatedKey = response.lastEvaluatedKey();
                if (lastEvaluatedKey != null) {
                    lastEvaluatedPk = lastEvaluatedKey.get(ATTRIBUTE_PK);
                }
            }
        } while(lastEvaluatedPk != null);
        if (LOG.isInfoEnabled()) {
            LOG.info("#{}", result.size());
        }
        return result;
    }

    @NonNull
    private <T> List<T> fromResponse(@NonNull Class<T> cls,
                                     @NonNull QueryResponse response) {
        List<Map<String, AttributeValue>> items = response.items();
        List<T> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(items)) {
            for (Map<String, AttributeValue> item : items) {
                result.add(fromItem(cls, item));
            }
        }
        return result;
    }

    @NonNull
    private <T> T fromItem(@NonNull Class<T> cls,
                           @NonNull Map<String, AttributeValue> item) {
        BeanIntrospection<T> introspection = BeanIntrospection.getIntrospection(cls);
        BeanIntrospection.Builder<T> builder = introspection.builder();
        for (String propertyName : introspection.getPropertyNames()) {
            builder.with(propertyName, parseArgument(item, propertyName, introspection).orElse(null));
        }
        return builder.build();
    }

    private <T> Optional<Object> parseArgument(@NonNull Map<String, AttributeValue> item,
                                               @NonNull String propertyName,
                                               @NonNull BeanIntrospection<T> introspection) {
        AttributeValue attributeValue = item.get(propertyName);
        if (attributeValue == null) {
            return Optional.empty();
        }
        Optional<? extends BeanProperty<?, Object>> beanPropertyOptional = introspection.getProperty(propertyName);
        if (beanPropertyOptional.isEmpty()) {
            return Optional.empty();
        }
        BeanProperty<?, Object> beanProperty = beanPropertyOptional.get();
        if (CharSequence.class.isAssignableFrom(beanProperty.getType())) {
            return Optional.of(attributeValue.s());
        }
        return Optional.empty();
    }


}