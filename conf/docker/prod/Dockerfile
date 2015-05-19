FROM ubuntu:14.04
MAINTAINER Peter Andersen <peter@sproutup.co>
RUN apt-get update && apt-get install -y && rm -rf /var/lib/apt/lists/*

# Default to UTF-8 file.encoding
ENV LANG C.UTF-8

ENV JAVA_VERSION 7u75
ENV JAVA_DEBIAN_VERSION 7u75-2.5.4-2

RUN apt-get update && apt-get install -y openjdk-7-jre && rm -rf /var/lib/apt/lists/*

ADD files /
WORKDIR /opt/docker
RUN ["chown", "-R", "daemon", "."]
USER daemon
ENV AWS_ACCESS_KEY_ID=AKIAJM5X5NV444LJEUSA
ENV AWS_SECRET_KEY=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
EXPOSE 9000
ENTRYPOINT ["bin/sproutup", "-Dconfig.file=conf/application-prod.conf"]
CMD []
