activator warn clean docker:stage
cp -R conf/docker/master/. target/docker/
cd target/docker
eb init -r us-west-2 -p docker -k endurance sproutup
#eb deploy
eb create develop -r us-west-2 -c develop -t WebServer -i t2.small -s -k endurance
