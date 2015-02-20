#!/bin/bash

path=target/universal/stage

cd sproutup_mvp

sudo kill -SIGTERM `sudo cat target/universal/stage/RUNNING_PID`

sudo activator clean stage -mem 512

sudo target/universal/stage/bin/sproutup -Dconfig.file=conf/application-master.conf &
