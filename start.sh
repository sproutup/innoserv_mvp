#!/bin/bash

path=target/universal/stage

if [ -f $path/RUNNING_PID ]; then
    echo "Running pid found!"
    sudo kill -SIGTERM `sudo cat $path/RUNNING_PID`
fi

sudo $path/bin/sproutup -Dconfig.file=conf/application-prod.conf -J-javaagent:/home/ubuntu/newrelic/newrelic.jar &
