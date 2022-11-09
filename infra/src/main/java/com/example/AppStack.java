package com.example;

import io.micronaut.aws.cdk.function.MicronautFunction;
import io.micronaut.aws.cdk.function.MicronautFunctionFile;
import io.micronaut.starter.application.ApplicationType;
import io.micronaut.starter.options.BuildTool;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.EndpointConfiguration;
import software.amazon.awscdk.services.apigateway.EndpointType;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.GlobalSecondaryIndexProps;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Tracing;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.constructs.Construct;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.micronauttodo.repositories.dynamodb.constants.DynamoDbConstants.ATTRIBUTE_GSI_1_PK;
import static com.micronauttodo.repositories.dynamodb.constants.DynamoDbConstants.ATTRIBUTE_GSI_1_SK;
import static com.micronauttodo.repositories.dynamodb.constants.DynamoDbConstants.ATTRIBUTE_GSI_2_PK;
import static com.micronauttodo.repositories.dynamodb.constants.DynamoDbConstants.ATTRIBUTE_GSI_2_SK;
import static com.micronauttodo.repositories.dynamodb.constants.DynamoDbConstants.ATTRIBUTE_PK;
import static com.micronauttodo.repositories.dynamodb.constants.DynamoDbConstants.ATTRIBUTE_SK;
import static com.micronauttodo.repositories.dynamodb.constants.DynamoDbConstants.INDEX_GSI_1;
import static com.micronauttodo.repositories.dynamodb.constants.DynamoDbConstants.INDEX_GSI_2;

public class AppStack extends Stack {
    public static final int MEMORY_SIZE = 2024;
    public static final int TIMEOUT = 20;

    public AppStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public AppStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);
        Table table = createTable("mntodo-java-table");
        Map<String, String> env = environmentVariables(table);
        Function javaFunction = createAppFunction("function-java", "mntodo-java-function", env, false).build();
        table.grantReadWriteData(javaFunction);
        LambdaRestApi javaFunctionApi = createRestApi("mntodo-java-api", javaFunction);

        CfnOutput.Builder.create(this, "JavaApiUrl")
                .exportName("JavaApiUrl")
                .value(javaFunctionApi.getUrl())
                .build();
    }

    private LambdaRestApi createRestApi(String id, Function function) {
        return LambdaRestApi.Builder.create(this, id)
                .handler(function)
                .endpointConfiguration(EndpointConfiguration.builder()
                        .types(Collections.singletonList(EndpointType.REGIONAL))
                        .build())
                .build();
    }

    private static Map<String, String> environmentVariables(Table table) {
        Map<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("DYNAMODB_TABLE_NAME", table.getTableName());
        // https://aws.amazon.com/blogs/compute/optimizing-aws-lambda-function-performance-for-java/
        environmentVariables.put("JAVA_TOOL_OPTIONS", "-XX:+TieredCompilation -XX:TieredStopAtLevel=1");
        return environmentVariables;
    }

    private Function.Builder createAppFunction(String moduleName,
                                               String id,
                                               Map<String, String> environmentVariables,
                                               boolean graalvm) {
        return createFunction(moduleName, id, environmentVariables, ApplicationType.DEFAULT, null, graalvm);
    }

    private Function.Builder createFunction(String moduleName,
                                            String id,
                                            Map<String, String> environmentVariables,
                                            ApplicationType applicationType,
                                            String handler,
                                            boolean graalvm) {
        Function.Builder builder =  MicronautFunction.create(applicationType,
                graalvm,
                this,
                id)
                .environment(environmentVariables)
                .code(Code.fromAsset(functionPath(moduleName, graalvm)))
                .timeout(Duration.seconds(TIMEOUT))
                .memorySize(MEMORY_SIZE)
                .tracing(Tracing.ACTIVE)
                .architecture(Architecture.X86_64)
                .logRetention(RetentionDays.FIVE_DAYS);
        return (handler != null) ? builder.handler(handler) : builder;
    }


    public static String functionPath(String moduleName, boolean graalvm) {
        return "../" + moduleName + "/build/libs/" + functionFilename(moduleName, graalvm);
    }

    public static String functionFilename(String moduleName, boolean graalvm) {
        return MicronautFunctionFile.builder()
                .graalVMNative(graalvm)
                .version("0.1")
                .archiveBaseName(moduleName)
                .buildTool(BuildTool.GRADLE)
                .build();
    }

    public Table createTable(String id) {
        Table table = Table.Builder.create(this, id)
                .partitionKey(Attribute.builder()
                        .name(ATTRIBUTE_PK)
                        .type(AttributeType.STRING)
                        .build())
                .sortKey(Attribute.builder()
                        .name(ATTRIBUTE_SK)
                        .type(AttributeType.STRING)
                        .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();
        table.addGlobalSecondaryIndex(globalSecondaryIndexProps(INDEX_GSI_1,
                ATTRIBUTE_GSI_1_PK,
                ATTRIBUTE_GSI_1_SK));
        table.addGlobalSecondaryIndex(globalSecondaryIndexProps(INDEX_GSI_2,
                ATTRIBUTE_GSI_2_PK,
                ATTRIBUTE_GSI_2_SK));
        return table;
    }

    private GlobalSecondaryIndexProps globalSecondaryIndexProps(String indexName,
                                                                String pk,
                                                                String sk) {
        return GlobalSecondaryIndexProps.builder()
                .indexName(indexName)
                .partitionKey(Attribute.builder()
                        .name(pk)
                        .type(AttributeType.STRING)
                        .build())
                .sortKey(Attribute.builder()
                        .name(sk)
                        .type(AttributeType.STRING)
                        .build())
                .build();
    }
}