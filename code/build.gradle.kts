plugins {
  id("com.micronauttodo.build.internal.lib-module")
  id("com.micronauttodo.build.internal.test-module")
}
val micronautVersion: String by project
dependencies {
  annotationProcessor("io.micronaut.openapi:micronaut-openapi")
  annotationProcessor("io.micronaut.security:micronaut-security-annotations")
  annotationProcessor("io.micronaut:micronaut-http-validation")
  compileOnly("com.google.code.findbugs:jsr305")

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

  testImplementation(project(":dev-localstack"))
  testImplementation("io.micronaut.aws:micronaut-function-aws-api-proxy")
}
tasks.withType<Test> {
  useJUnitPlatform()
}
java {
  sourceCompatibility = JavaVersion.toVersion("11")
  targetCompatibility = JavaVersion.toVersion("11")
}
configurations.all {
  resolutionStrategy {
    force("io.micronaut.aws:micronaut-aws-sdk-v2:3.8.2")
    force("io.micronaut.aws:micronaut-function-aws-api-proxy:3.8.2")
  }
}

