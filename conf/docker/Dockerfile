FROM sproutupco/alpine-java-openjdk
MAINTAINER Peter Andersen <peter@sproutup.co>

COPY files /
WORKDIR /opt/docker
RUN ["chown", "-R", "daemon", "."]
USER daemon
ENTRYPOINT ["bin/sproutup", "-Dconfig.file=conf/docker.conf"]
CMD []
