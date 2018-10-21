#!/bin/bash

echo '--------------- USER DATA SCRIPT STARTED -------------'

echo 'Step 1: Changing Java to 1.8.0'
yum install java-1.8.0-openjdk -y
yum remove java-1.7.0-openjdk -y

instance_id=$(curl http://169.254.169.254/latest/meta-data/instance-id)

application=$(aws ec2 describe-tags \
  --filters "Name=resource-type, Values=instance" "Name=resource-id, Values=${instance_id}" "Name=key, Values=application" \
  --region=us-west-2 \
  --output=text | cut -f5)

version=$(aws ec2 describe-tags \
  --filters "Name=resource-type, Values=instance" "Name=resource-id, Values=${instance_id}" "Name=key, Values=version" \
  --region=us-west-2 \
  --output=text | cut -f5)

environment=$(aws ec2 describe-tags \
  --filters "Name=resource-type, Values=instance" "Name=resource-id, Values=${instance_id}" "Name=key, Values=environment" \
  --region=us-west-2 \
  --output=text | cut -f5)
  
congnito_secret=$(aws ec2 describe-tags \
  --filters "Name=resource-type, Values=instance" "Name=resource-id, Values=${instance_id}" "Name=key, Values=cognito_secret" \
  --region=us-west-2 \
  --output=text | cut -f5)

artifact_key="$application-$version.jar"

echo "Step 2: Downloading artifacts for $application application, version $version: $artifact_key"
aws s3api get-object --bucket artifacts-hm --key ${artifact_key} /tmp/${artifact_key}

echo "Step 3: Setting environment variables"
export COGNITO_CLIENT_SECRET=${congnito_secret}

echo "Step 4: Running $application on $environment environment"
java -jar -Dspring.profiles.active=${environment} /tmp/${artifact_key}

echo '--------------- USER DATA SCRIPT FINISHED -------------'