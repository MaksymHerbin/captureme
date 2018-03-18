#!/bin/bash

echo '--------------- USER DATA SCRIPT STARTED -------------'

echo 'Step 1: Changing Java to 1.8.0'
yum install java-1.8.0-openjdk -y
yum remove java-1.7.0-openjdk -y

echo 'Step 2: Copy runnable JAR from S3'
aws s3api get-object --bucket captureme-ci-artifacts --key gateway-0.0.1-SNAPSHOT.jar /tmp/gateway.jar

echo 'Step 3: Running Java App'
java -jar /tmp/gateway.jar

echo '--------------- USER DATA SCRIPT FINISHED -------------'