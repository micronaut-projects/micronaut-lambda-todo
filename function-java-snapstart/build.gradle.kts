plugins {
    id("com.micronauttodo.build.internal.lambda-java")
    id("com.micronauttodo.build.internal.test-module")
}
dependencies {
    implementation(projects.code)
    implementation("io.micronaut.crac:micronaut-crac")
}
application {
    mainClass.set("com.example.Application")
}