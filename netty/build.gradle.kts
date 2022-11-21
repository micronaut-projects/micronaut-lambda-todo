plugins {
    id("com.micronauttodo.build.internal.netty")
}
version = "0.1"
dependencies {
  implementation(projects.code)
    implementation(projects.codeGraal)
  implementation(projects.devSecurity)
  implementation(projects.devLocalstack)
}
application {
    mainClass.set("example.micronaut.Application")
}