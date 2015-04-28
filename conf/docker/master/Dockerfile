FROM dockerfile/java
MAINTAINER Peter Andersen <peter@sproutup.co>
# Usage: ADD [source directory or URL] [destination directory]
ADD files /
WORKDIR /opt/docker
RUN ["chown", "-R", "daemon", "."]
USER daemon
ENV AWS_ACCESS_KEY_ID=AKIAJM5X5NV444LJEUSA
ENV AWS_SECRET_KEY=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
EXPOSE 9000
ENTRYPOINT ["bin/sproutup", "-Dconfig.file=conf/application-master.conf"]
CMD []
