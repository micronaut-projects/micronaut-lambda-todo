plugins {
    id("com.micronauttodo.build.internal.netty")
}

version = "0.1"

dependencies {
    implementation(projects.code)
    implementation(projects.codeGraal)
    implementation(projects.devSecurity)
    implementation(projects.devLocalstack)
    runtimeOnly("org.yaml:snakeyaml")
}

application {
    mainClass.set("example.micronaut.Application")
}