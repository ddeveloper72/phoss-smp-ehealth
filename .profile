#!/bin/bash
export JAVA_OPTS="${JAVA_OPTS} -Dmongodb.connectionstring=${MONGODB_URI} -Dmongodb.dbname=${MONGODB_DBNAME}"
echo "MongoDB URI being set to: ${MONGODB_URI}"
echo "MongoDB DB being set to: ${MONGODB_DBNAME}"
