#!/bin/bash

path=target/universal/stage

sudo kill -SIGTERM `sudo cat $path/RUNNING_PID`

sudo $path/bin/sproutup -Dconfig.file=conf/application-prod.conf -J-javaagent:/home/ubuntu/newrelic/newrelic.jar &
