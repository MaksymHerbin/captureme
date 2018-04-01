#!/usr/bin/env bash

java -jar src/main/resources/liquibase/lib/liquibase.jar --defaultsFile src/main/resources/liquibase/liquibase.properties $*