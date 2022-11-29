plugins {
  id("com.micronauttodo.build.internal.lib-module")
  id("com.micronauttodo.build.internal.test-module")
}
val micronautVersion: String by project
dependencies {
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
