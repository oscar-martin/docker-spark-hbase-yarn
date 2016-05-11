FROM ubuntu:14.04
MAINTAINER oscarmartinvicente@gmail.com

# Install
RUN apt-get update && apt-get install -y apt-transport-https \
    && echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list \
    && apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823 \
    && apt-get update && apt-get install -y --fix-missing \
    openjdk-7-jre-headless \
    openjdk-7-jdk \
    git \
    sbt \
    curl \
    && apt-get clean && apt-get autoclean && apt-get -y autoremove

# Spark
RUN curl -s http://d3kbcqa49mib13.cloudfront.net/spark-1.5.1-bin-hadoop2.6.tgz | tar -xz -C /usr/local/
RUN cd /usr/local && ln -s spark-1.5.1-bin-hadoop2.6 spark
ENV SPARK_HOME /usr/local/spark

RUN mkdir -p /usr/local/spark/yarnconf
ADD core-site.xml /usr/local/spark/yarnconf/
ADD yarn-site.xml /usr/local/spark/yarnconf/
ENV YARN_CONF_DIR /usr/local/spark/yarnconf

VOLUME /project
WORKDIR /project

ARG HOSTNAME
ARG NAMENODE_PORT

RUN sed -i s/NAMENODE_PORT/$NAMENODE_PORT/ $YARN_CONF_DIR/core-site.xml
RUN sed -i s/HOSTNAME/$HOSTNAME/ $YARN_CONF_DIR/core-site.xml
RUN sed -i s/HOSTNAME/$HOSTNAME/ $YARN_CONF_DIR/yarn-site.xml

ENTRYPOINT ["sbt"]

#ADD bootstrap.sh /etc/bootstrap.sh
#RUN chown root:root /etc/bootstrap.sh
#RUN chmod 700 /etc/bootstrap.sh

#CMD ["/etc/bootstrap.sh"]
