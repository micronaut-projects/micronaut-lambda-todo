plugins {
    id("com.micronauttodo.build.internal.lambda-native")
    id("com.micronauttodo.build.internal.test-module")
}
dependencies {
    implementation(projects.code)
}
application {
    mainClass.set("com.example.Application")
}