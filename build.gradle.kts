buildscript {
    dependencies {
        classpath("com.github.docker-java:docker-java-transport-httpclient5:3.2.13") {
            because("M1 macs need a later version of JNA")
        }
    }
}

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
    id("io.micronaut.library") version "4.0.3" apply false
    id("io.micronaut.minimal.application") version "4.0.3" apply false
    id("io.micronaut.application") version "4.0.3" apply false
}
