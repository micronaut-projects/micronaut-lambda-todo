package com.example;

import org.junit.jupiter.api.Test;
import software.amazon.awscdk.App;
import software.amazon.awscdk.assertions.Template;
import java.io.File;
import java.util.Collections;

class AppStackTest {

    @Test
    void testAppStack() {
        if (
                new File(AppStack.functionPath("function-java-snapstart", Runtime.JAVA)).exists() &&
                new File(AppStack.functionPath("function-native", Runtime.GRAALVM)).exists() &&
                new File(AppStack.functionPath("function-java", Runtime.JAVA)).exists()) {
            AppStack stack = new AppStack(new App(), "TestMnHelloWorldStack");
            Template template = Template.fromStack(stack);
            template.hasResourceProperties("AWS::Lambda::Function", Collections.singletonMap("Handler", "io.micronaut.function.aws.proxy.MicronautLambdaHandler"));
        }
    }
}
