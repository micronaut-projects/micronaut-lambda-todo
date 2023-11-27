plugins {
    id("com.micronauttodo.build.internal.lib-module")
    id("com.micronauttodo.build.internal.test-module")
}

val micronautVersion: String by project

dependencies {
    annotationProcessor("io.micronaut.openapi:micronaut-openapi")
    annotationProcessor("io.micronaut.security:micronaut-security-annotations")
    annotationProcessor("io.micronaut:micronaut-http-validation")
    implementation("io.micronaut:micronaut-http-server")

    // remove when the following dependencies when there is aws version with this https://github.com/micronaut-projects/micronaut-aws/pull/1963
    implementation("io.micronaut.servlet:micronaut-servlet-core")
    implementation("io.micronaut.aws:micronaut-function-aws-api-proxy")

    implementation("io.micronaut.views:micronaut-views-thymeleaf")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut.aws:micronaut-aws-sdk-v2")
    implementation("io.micronaut.aws:micronaut-aws-apigateway")
    implementation("software.amazon.awssdk:dynamodb") {
        exclude(group = "software.amazon.awssdk", module = "apache-client")
        exclude(group = "software.amazon.awssdk", module = "netty-nio-client")
    }
    implementation("software.amazon.awssdk:url-connection-client")
    implementation("com.github.ksuid:ksuid:1.1.1")
    implementation("io.swagger.core.v3:swagger-annotations")
    implementation("io.micronaut:micronaut-management")
    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("io.micronaut.security:micronaut-security-oauth2")
    implementation("io.micronaut:micronaut-http-client-jdk")
    implementation(platform("com.amazonaws:aws-xray-recorder-sdk-bom:2.13.0"))
    compileOnly("com.amazonaws:aws-xray-recorder-sdk-aws-sdk-v2")
    testImplementation(projects.devLocalstack)
    testImplementation("io.micronaut.aws:micronaut-function-aws-api-proxy")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.toVersion("21")
    targetCompatibility = JavaVersion.toVersion("21")
}

repositories {
    mavenCentral()
}
