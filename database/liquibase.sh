#!/usr/bin/env bash

function prop {
    grep "${1}" ${2}|cut -d'=' -f2
}

properties_file=environments/$1/connection.properties

if [ -f ${properties_file} ]; then
   echo "Loading database connection properties from ${properties_file}"

   url=$(prop 'url' ${properties_file})
   username=$(prop 'username' ${properties_file})
   password=$(prop 'password' ${properties_file})

   if [ -f ${url} ]; then
        echo "Could not find property url in ${properties_file}"
        exit 1
   fi

   if [ -f ${username} ]; then
        echo "Could not find property username in ${properties_file}"
        exit 1
   fi

   if [ -f ${password} ]; then
        echo "Could not find property password in ${properties_file}"
        exit 1
   fi

   java -jar lib/liquibase.jar \
        --defaultsFile liquibase.properties \
        --username ${username} \
        --password ${password} \
        --url ${url} \
        ${2}

else
   echo "File ${properties_file} does not exist. Please make sure you are using correct environment and file exists in specified path"
   echo "Awailable environments:"
   echo "development"
   echo "local"
   exit 1
fi

