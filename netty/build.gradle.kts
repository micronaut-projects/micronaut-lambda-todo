plugins {
    id("com.micronauttodo.build.internal.netty")
}
version = "0.1"
dependencies {
  implementation(projects.code)
}
application {
    mainClass.set("example.micronaut.Application")
}