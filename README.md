## Micronaut Framework AWS Lambda Deployments

![Architecture diagram](architecture.png)

This is a serverless application built in Java with the [Micronaut Framework](https://micronaut.io). It consists of an [Amazon API Gateway](https://aws.amazon.com/api-gateway/) backed by an [AWS Lambda](https://aws.amazon.com/lambda/) function and an [Amazon DynamoDB](https://aws.amazon.com/dynamodb/) table for storage.

## Deployments

This application compares four deployment scenarios:

- FAT Jar to Java Runtime
- FAT Jar to Java Runtime + SnapStart
- FAT Jar to Java Runtime + SnapStart + Priming
- Native executable built with [GraalVM](https://graalvm.org)  to a [custom AWS Lambda runtime](https://docs.aws.amazon.com/lambda/latest/dg/runtimes-custom.html).

## Module Architecture

Code is shared between every deployment. 

![Module Distribution](module-distribution.png)

## Requirements

- [AWS CLI](https://aws.amazon.com/cli/)
- [AWS CDK](https://aws.amazon.com/cdk/)
- Java 17

## Build

Micronaut framework is build-agnostic. You can build applications with [Maven](https://maven.apache.org) or [Gradle](https://gradle.org). However, this project uses with Gradle. 

## Deployment

Deploy the demo to your AWS account using [AWS CDK](https://aws.amazon.com/cdk/). Module `infra` contains the CDK code.

If you have never run CDK in your AWS account, you will have to `cdk bootstrap` first.

To ease deployment, the project contains a bash script. You can deploy via: 

```bash
./release.sh
```

The bash script builds the FAT JARs and the native executable with GraalVM, and runs `cdk deploy`. It uses AWS CloudFormation to deploy the resources to your account.

CDK creates three outputs with the API Gateway endpoint URLs to use in our load tests.

## Load Tests

- The application uses [Gatling](https://gatling.io/) to load test the application. Module `loadtests` contains the Gatling load tests code.

The load test executes a simulation which runs a POST, GET, DELETE scenario with 50 concurrent users for 3 minutes and then ramps up to 100 concurrent users for extra 2 minutes.    

After deployment, you can run the load script with: 

```bash
./load.sh
```

This is a demanding load test, to change it. Edit `TodoSimulation.java` 

### CloudWatch Logs Insights

Using [CloudWatch Logs Insights](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/AnalyzingLogData.html), you can analyse the latency of the requests made to the Lambda functions.

### Max cold start with Cloud Watch Log Insights

If you are not using SnapStart run: 

```
filter @type="REPORT"
| fields greatest(@initDuration, 0) + @duration as duration
| max(duration) as max
```

with SnapStart use: 

```
filter @message like "REPORT"
| filter @message not like "RESTORE_REPORT"
| parse @message /Restore Duration: (?<@restore_duration_ms>[0-9\.]+)/
| parse @message / Duration: (?<@invoke_duration_ms>[0-9\.]+)/
| fields
greatest(@restore_duration_ms, 0) as restore_duration_ms,
greatest(@invoke_duration_ms, 0) as invoke_duration_ms
| fields
restore_duration_ms + invoke_duration_ms as total_invoke_ms
| stat
max(total_invoke_ms) as max
```

### Separate Cold Starts without SnapStart

The query separates cold starts from other requests and then gives you p50, p90 and p99 percentiles.

```
filter @type="REPORT"
| fields greatest(@initDuration, 0) + @duration as duration, ispresent(@initDuration) as coldStart
| stats count(*) as count, pct(duration, 50) as p50, pct(duration, 90) as p90, pct(duration, 99) as p99, max(duration) as max by coldStart
```

### Percentiles with SnapStart

if you are using SnapStart you can use: 

```
filter @message like "REPORT"
| filter @message not like "RESTORE_REPORT"
| parse @message /Restore Duration: (?<@restore_duration_ms>[0-9\.]+)/
| parse @message / Duration: (?<@invoke_duration_ms>[0-9\.]+)/
| fields
greatest(@restore_duration_ms, 0) as restore_duration_ms,
greatest(@invoke_duration_ms, 0) as invoke_duration_ms
| fields
restore_duration_ms + invoke_duration_ms as total_invoke_ms
| stat
pct(total_invoke_ms, 50) as total_invoke_ms_p50,
pct(total_invoke_ms, 99) as total_invoke_ms_p99,
pct(total_invoke_ms, 99.9) as total_invoke_ms_p99.9,
max(total_invoke_ms) as max
```

#### CloudWatch Log Insights Java Runtime

![CloudWatch Logs Insights results for Java Runtime](cloudwatch-log-insights-java.png)

#### CloudWatch Log Insights Java Runtime + SnapStart

![CloudWatch Logs Insights results for Java Runtime](cloudwatch-log-insights-java-snapstart.png)

#### CloudWatch Log Insights Custom Runtime

![CloudWatch Logs Insights results for Native Executable build with GraalVM in AWS Lambda Custom runtime](cloudwatch-log-insights-native.png)


## Destroy

To destroy the CDK stack run: 

```bash
./destroy.sh
```


## Measurements

| Runtime                | Max Cold Startup ms | 
|:-----------------------|:--------------------|
| Java runtime           | `8243`              |
| Native                 | `922`               |
| Java SnapStart         | `3625`              |
| Java SnapStart Priming | `3493`              |
