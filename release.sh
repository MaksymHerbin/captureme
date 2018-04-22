#!/bin/bash

application=$1

echo "Releasing $application"

cd ${application}

mvn package -DskipTests
aws s3 cp target/${application}-0.0.1-SNAPSHOT.jar s3://artifacts-hm/