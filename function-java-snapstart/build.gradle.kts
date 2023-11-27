plugins {
    id("com.micronauttodo.build.internal.lambda-java")
    id("com.micronauttodo.build.internal.test-module")
}

dependencies {
    implementation(projects.devSecurity)
    implementation(projects.code)
    implementation("io.micronaut.crac:micronaut-crac")
    testImplementation(projects.devLocalstack)
    testImplementation("io.micronaut.security:micronaut-security-oauth2")
}

application {
    mainClass.set("com.example.Application")

}