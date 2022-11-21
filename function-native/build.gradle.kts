plugins {
    id("com.micronauttodo.build.internal.lambda-native")
    id("com.micronauttodo.build.internal.test-module")
}
dependencies {
    implementation(projects.devSecurity)
    implementation(projects.code)
    implementation(projects.codeGraal)
    testImplementation(projects.devLocalstack)
    testImplementation("io.micronaut.security:micronaut-security-oauth2")
}
application {
    mainClass.set("com.example.Application")
}