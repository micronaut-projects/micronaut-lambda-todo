rootProject.name = "micronaut-lambda-todo-app"

include("netty")
include("code")
include("code-graal")
include("infra")
include("dev-security")
include("dev-localstack")
include("repositories-dynamodb-constants")
include("function-java")
include("function-java-snapstart")
include("function-java-snapstart-priming")
include("function-native")
include("loadtests")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
