plugins {
    id("com.micronauttodo.build.internal.lib-module")
}
dependencies {
    implementation(projects.repositoriesDynamodbConstants)
    compileOnly("software.amazon.awssdk:dynamodb")
}