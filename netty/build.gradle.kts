plugins {
    id("com.micronauttodo.build.internal.netty")
}
version = "0.1"
dependencies {
    implementation("io.micronaut.aws:micronaut-aws-sdk-v2")
    /*
    implementation(projects.devSecurity)
    implementation(projects.code)
    implementation(projects.devLocalstack)
     */
}
application {
    mainClass.set("com.example.Application")
}