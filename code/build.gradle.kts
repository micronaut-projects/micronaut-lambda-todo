plugins {
  id("com.micronauttodo.build.internal.lib-module")
  id("com.micronauttodo.build.internal.test-module")
}
val micronautVersion: String by project
dependencies {
  annotationProcessor("io.micronaut.openapi:micronaut-openapi")
  annotationProcessor("io.micronaut.security:micronaut-security-annotations")
  annotationProcessor("io.micronaut:micronaut-http-validation")
  implementation("io.micronaut:micronaut-jackson-databind")
  implementation("io.micronaut.aws:micronaut-aws-sdk-v2")
  implementation("software.amazon.awssdk:dynamodb") {
    exclude(group = "software.amazon.awssdk", module = "apache-client")
    exclude(group = "software.amazon.awssdk", module = "netty-nio-client")
  }
  implementation("software.amazon.awssdk:url-connection-client")
  implementation("com.github.ksuid:ksuid:1.1.1")
  implementation("io.swagger.core.v3:swagger-annotations")
  implementation("io.micronaut:micronaut-http-server")
  implementation("io.micronaut:micronaut-management")
  implementation("io.micronaut.views:micronaut-views-thymeleaf")
  implementation("io.micronaut.security:micronaut-security-jwt")
  implementation("io.micronaut.security:micronaut-security-oauth2")

  implementation(platform("com.amazonaws:aws-xray-recorder-sdk-bom:2.13.0"))
  implementation("com.amazonaws:aws-xray-recorder-sdk-aws-sdk-v2")
  compileOnly("com.amazonaws.serverless:aws-serverless-java-container-core:1.9")
  testImplementation(projects.devLocalstack)
  testImplementation("io.micronaut.aws:micronaut-function-aws-api-proxy")
}
tasks.withType<Test> {
  useJUnitPlatform()
}
java {
  sourceCompatibility = JavaVersion.toVersion("11")
  targetCompatibility = JavaVersion.toVersion("11")
}
repositories {
  mavenCentral()
}
