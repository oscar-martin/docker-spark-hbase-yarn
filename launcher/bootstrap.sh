#!/bin/bash

# altering the core-site configuration
sed -i s/NAMENODE_PORT/$NAMENODE_PORT/ $YARN_CONF_DIR/core-site.xml
sed -i s/HOSTNAME/$HOSTNAME/ $YARN_CONF_DIR/core-site.xml
sed -i s/HOSTNAME/$HOSTNAME/ $YARN_CONF_DIR/yarn-site.xml

exec sbt
