#!/bin/bash

#build the play application
sudo activator info clean stage -mem 512

#copy the start script and config file to the staging area
cp play target/universal/stage/bin/play
chmod +x target/universal/stage/bin/play
cp play.conf.prod target/universal/stage/bin/play.conf
