#!/bin/bash
EXIT_STATUS=0
./gradlew test || EXIT_STATUS=$?
if [ $EXIT_STATUS -ne 0 ]; then
 exit $EXIT_STATUS
fi
./gradlew :function-java:shadowJar || EXIT_STATUS=$?
if [ $EXIT_STATUS -ne 0 ]; then
 exit $EXIT_STATUS
fi
cd infra
cdk deploy --profile=softamodev --require-approval never || EXIT_STATUS=$?
if [ $EXIT_STATUS -ne 0 ]; then
  exit $EXIT_STATUS
fi
cd ..
exit $EXIT_STATUS
