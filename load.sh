#!/bin/bash
STACK_NAME=MnTodoStack

export API_URL="$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query 'Stacks[0].Outputs[?OutputKey==`JavaApiUrl`].OutputValue' --output text)"
echo API_URL
./gradlew :loadtests:gatlingRun

export API_URL="$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query 'Stacks[0].Outputs[?OutputKey==`NativeApiUrl`].OutputValue' --output text)"
./gradlew :loadtests:gatlingRun

export API_URL="$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query 'Stacks[0].Outputs[?OutputKey==`SnapStartApiUrl`].OutputValue' --output text)"
./gradlew :loadtests:gatlingRun

export API_URL="$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query 'Stacks[0].Outputs[?OutputKey==`SnapStartPrimingApiUrl`].OutputValue' --output text)"
./gradlew :loadtests:gatlingRun

