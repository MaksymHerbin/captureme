#!/bin/bash

echo '--------------- USER DATA SCRIPT STARTED -------------'

echo 'Step 1: Changing Java to 1.8.0'
yum install java-1.8.0-openjdk -y
yum remove java-1.7.0-openjdk -y

application=$(ec2-describe-tags \
  --region=us-west-2 \
  --filter "resource-type=instance" \
  --filter "resource-id=$(ec2-metadata -i | cut -d ' ' -f2)" \
  --filter "key=application" | cut -f5)

version=$(ec2-describe-tags \
  --region=us-west-2 \
  --filter "resource-type=instance" \
  --filter "resource-id=$(ec2-metadata -i | cut -d ' ' -f2)" \
  --filter "key=version" | cut -f5)

environment=$(ec2-describe-tags \
  --region=us-west-2 \
  --filter "resource-type=instance" \
  --filter "resource-id=$(ec2-metadata -i | cut -d ' ' -f2)" \
  --filter "key=environment" | cut -f5)

artifact_key="$application-$version.jar"


echo 'Step 2: Copy runnable JAR from S3'
aws s3api get-object --bucket artifacts-hm --key ${artifact_key} /tmp/${artifact_key}

echo 'Step 3: Running Java App'
java -jar -Dspring.profiles.active=${environment} /tmp/${artifact_key}

echo '--------------- USER DATA SCRIPT FINISHED -------------'