rootProject.name="micronaut-lambda-todo"

include("infra")
include("code")
include("dev-localstack")
include("dev-security")
include("function-java")
include("function-java-snapstart")
include("function-native")
include("netty")
include("repositories-dynamodb-constants")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
