plugins {
    id("com.micronauttodo.build.internal.lambda-java")
    id("com.micronauttodo.build.internal.test-module")
}
dependencies {
    implementation(projects.code)
}
application {
    mainClass.set("com.example.Application")
}