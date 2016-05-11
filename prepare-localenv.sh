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

    echo "Updating /etc/hosts to make ${DOCKER_CONTAINER} point to ${IP} locally"
    if grep "${DOCKER_CONTAINER}" /etc/hosts >/dev/null; then
      sudo sed -i '' "s/^.*${DOCKER_CONTAINER}.*\$/${IP} ${DOCKER_CONTAINER}/" /etc/hosts
    else
      sudo sh -c "echo '${IP} ${DOCKER_CONTAINER}' >> /etc/hosts"
    fi

done

DOCKER_CONTAINER="hbase-master"

echo "Updating /etc/hosts to make ${DOCKER_CONTAINER} point to ${IP} in docker-machine"
ETC_HOST_LOCALHOST_LINE="127.0.0.1 localhost localhost.local"
REGEX_ETC_HOST_LOCALHOST_LINE="^.*127.0.0.1.*\$"
if docker-machine ssh "${LOCAL_VM_NAME}" "grep '${DOCKER_CONTAINER}' /etc/hosts >/dev/null"; then
  eval $(docker-machine ssh "${LOCAL_VM_NAME}" "sudo sed -i 's/^.*${DOCKER_CONTAINER}.*\$/${IP} ${DOCKER_CONTAINER} ${LOCAL_VM_NAME}/' /etc/hosts")
  eval $(docker-machine ssh "${LOCAL_VM_NAME}" "sudo sed -i 's/${REGEX_ETC_HOST_LOCALHOST_LINE}/${ETC_HOST_LOCALHOST_LINE}/' /etc/hosts")
else
  docker-machine ssh "${LOCAL_VM_NAME}" "sudo sh -c \"echo '${IP} ${DOCKER_CONTAINER} ${LOCAL_VM_NAME}' >> /etc/hosts\""
  docker-machine ssh "${LOCAL_VM_NAME}" "sudo sed -i 's/${REGEX_ETC_HOST_LOCALHOST_LINE}/${ETC_HOST_LOCALHOST_LINE}/' /etc/hosts"
fi


echo "Execute: docker-compose up -d"
