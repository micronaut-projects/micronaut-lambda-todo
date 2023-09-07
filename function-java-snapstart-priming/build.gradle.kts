plugins {
    id("com.micronauttodo.build.internal.lambda-java")
    id("com.micronauttodo.build.internal.test-module")
}

dependencies {
    implementation(projects.devSecurity)
    implementation(projects.code)
    implementation("io.micronaut.crac:micronaut-crac")
    implementation("io.micronaut.security:micronaut-security-jwt")
    runtimeOnly("org.yaml:snakeyaml")

    testImplementation(projects.devLocalstack)
    testImplementation("io.micronaut.security:micronaut-security-oauth2")
    testRuntimeOnly("org.yaml:snakeyaml")
}

application {
    mainClass.set("com.example.Application")
}