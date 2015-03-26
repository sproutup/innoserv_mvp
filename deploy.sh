#!/bin/bash

path=target/universal/stage

sudo kill -SIGTERM `sudo cat $path/RUNNING_PID`

sudo activator clean stage -mem 512

sudo target/universal/stage/bin/sproutup -Dconfig.file=conf/application-master.conf -J-javaagent:/home/ubuntu/newrelic/newrelic.jar &
