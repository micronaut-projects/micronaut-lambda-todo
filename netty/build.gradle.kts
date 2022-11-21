plugins {
    id("com.micronauttodo.build.internal.base-module")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.6.5"
}

version = "0.1"

dependencies {
    runtimeOnly("ch.qos.logback:logback-classic")
    implementation(projects.devSecurity)
    implementation(projects.code)
    implementation(projects.devLocalstack)
}

application {
    mainClass.set("com.example.Application")
}
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.example.*")
    }
}