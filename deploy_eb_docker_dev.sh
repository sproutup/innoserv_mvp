activator warn clean docker:stage
cp -R conf/docker/master/. target/docker/
cd target/docker
eb init -r us-west-2 -p docker -k endurance dev
eb deploy
#eb create test -r us-west-2 -c test -t WebServer -i t2.small -s -k endurance
