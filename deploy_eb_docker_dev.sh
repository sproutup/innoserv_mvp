activator warn clean docker:stage
cp -R conf/docker/master/. target/docker/
cd target/docker
eb init -r us-west-2 -p docker -k endurance develop
eb deploy 
#eb create test -r us-west-2 -c test -t WebServer -i t2.small -s -k endurance
#eb create develop -r us-west-2 -c develop-sproutup-co -t WebServer -i t2.small -s -k endurance

