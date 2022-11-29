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
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Tracing;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.constructs.Construct;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AppStack extends Stack {
    public static final int MEMORY_SIZE = 2024;
    public static final int TIMEOUT = 20;

    public AppStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public AppStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);
        createGatewayLambdaTable("function-java",
                "helloworld-java-function",
                "helloworld-java-api",
                "JavaApiUrl",
                Runtime.JAVA);
        createGatewayLambdaTable("function-native",
                "helloworld-native-function",
                "helloworld-native-api",
                "NativeApiUrl",
                Runtime.GRAALVM);
        createGatewayLambdaTable("function-java-snapstart",
                "helloworld-snapstart-function",
                "helloworld-snapstart-api",
                "SnapStartApiUrl",
                Runtime.JAVA_SNAP_START);
    }

    void createGatewayLambdaTable(String moduleName,
                                  String functionId,
                                  String apiId,
                                  String outputId,
                                  Runtime runtime){
        Map<String, String> env = environmentVariables();
        Function function = createAppFunction(moduleName, functionId, env, runtime).build();
        LambdaRestApi api = createRestApi(apiId, function);
        CfnOutput.Builder.create(this, outputId)
                .exportName(outputId)
                .value(api.getUrl())
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

    private static Map<String, String> environmentVariables() {
        Map<String, String> environmentVariables = new HashMap<>();
        // https://aws.amazon.com/blogs/compute/optimizing-aws-lambda-function-performance-for-java/
        environmentVariables.put("JAVA_TOOL_OPTIONS", "-XX:+TieredCompilation -XX:TieredStopAtLevel=1");
        return environmentVariables;
    }

    private Function.Builder createAppFunction(String moduleName,
                                               String id,
                                               Map<String, String> environmentVariables,
                                               Runtime runtime) {
        return createFunction(moduleName, id, environmentVariables, ApplicationType.DEFAULT, null, runtime);
    }

    private Function.Builder createFunction(String moduleName,
                                            String id,
                                            Map<String, String> environmentVariables,
                                            ApplicationType applicationType,
                                            String handler,
                                            Runtime runtime) {
        Function.Builder builder =  MicronautFunction.create(applicationType,
                        runtime == Runtime.GRAALVM,
                this,
                id)
                .environment(environmentVariables)
                .code(Code.fromAsset(functionPath(moduleName, runtime)))
                .timeout(Duration.seconds(TIMEOUT))
                .memorySize(MEMORY_SIZE)
                .tracing(Tracing.DISABLED)
                .architecture(Architecture.X86_64)
                .logRetention(RetentionDays.FIVE_DAYS);

        if (runtime == Runtime.JAVA_SNAP_START) {
            //builder = builder.snapstart(SnapStart.PUBLISHED_VERSIONS);
        }
        return (handler != null) ? builder.handler(handler) : builder;
    }

    public static String functionPath(String moduleName, Runtime runtime) {
        return "../" + moduleName + "/build/libs/" + functionFilename(moduleName, runtime);
    }

    public static String functionFilename(String moduleName, Runtime runtime) {
        return MicronautFunctionFile.builder()
                .graalVMNative(runtime == Runtime.GRAALVM)
                .version("0.1")
                .archiveBaseName(moduleName)
                .buildTool(BuildTool.GRADLE)
                .build();
    }
}