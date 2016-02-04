## FROM sproutupco/alpine-java-openjdk
FROM dockerfile/java
MAINTAINER Peter Andersen <peter@sproutup.co>

COPY files /
WORKDIR /opt/docker
RUN ["chown", "-R", "daemon", "."]
USER daemon

CMD ["bin/sproutup", "-Dconfig.file=conf/docker.conf"]

