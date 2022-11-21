plugins {
    id("com.micronauttodo.build.internal.lambda-java")
    id("com.micronauttodo.build.internal.test-module")
}
dependencies {
    implementation(projects.devSecurity)
    implementation(projects.code)
    testImplementation(projects.devLocalstack)
    testImplementation("io.micronaut.security:micronaut-security-oauth2")
    implementation("io.micronaut.crac:micronaut-crac")
}
application {
    mainClass.set("com.example.Application")
}