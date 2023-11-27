#!/bin/bash
EXIT_STATUS=0
EXIT_STATUS=0

architecture="$1"
if [ "$architecture" != "arm" ] && [ "$architecture" != "x86" ]; then
    echo "First parameter is not equal to 'arm' or 'x86'"
    exit 1
fi
export LAMBDA_ARCHITECTURE=$architecture

./gradlew test || EXIT_STATUS=$?
if [ $EXIT_STATUS -ne 0 ]; then
 exit $EXIT_STATUS
fi
./gradlew :function-native:buildNativeLambda -Pmicronaut.runtime=lambda_provided || EXIT_STATUS=$?
if [ $EXIT_STATUS -ne 0 ]; then
 exit $EXIT_STATUS
fi
./gradlew :function-java:shadowJar || EXIT_STATUS=$?
if [ $EXIT_STATUS -ne 0 ]; then
 exit $EXIT_STATUS
fi
./gradlew :function-java-snapstart:shadowJar || EXIT_STATUS=$?
if [ $EXIT_STATUS -ne 0 ]; then
 exit $EXIT_STATUS
fi
./gradlew :function-java-snapstart-priming:shadowJar || EXIT_STATUS=$?
if [ $EXIT_STATUS -ne 0 ]; then
 exit $EXIT_STATUS
fi
cd infra
cdk deploy || EXIT_STATUS=$?
if [ $EXIT_STATUS -ne 0 ]; then
  exit $EXIT_STATUS
fi
cd ..
exit $EXIT_STATUS
