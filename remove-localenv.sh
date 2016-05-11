#!/bin/bash
#
# Script to start docker and update the /etc/hosts file to point to
# the hbase-docker container
#
# set -x

LOCAL_VM_NAME=${DOCKER_MACHINE_NAME}

declare -a DOCKER_CONTAINERS=(
"hdfs-master"
"hbase-master"
"yarn-master"
${LOCAL_VM_NAME}
)

for DOCKER_CONTAINER in "${DOCKER_CONTAINERS[@]}"
do
    IP=$(docker-machine ip ${LOCAL_VM_NAME})

    echo "Deleting ${DOCKER_CONTAINER} entry from local /etc/hosts"
    if grep "${DOCKER_CONTAINER}" /etc/hosts >/dev/null; then
      sudo sed -i '' "s/^.*${DOCKER_CONTAINER}.*\$//" /etc/hosts
    fi

done

echo "Local /etc/hosts cleansed!"
