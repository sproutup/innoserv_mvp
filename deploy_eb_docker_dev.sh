activator warn clean docker:stage
cp -R conf/docker/master/. target/docker/
cd target/docker
eb init -r us-west-2 -p docker -k endurance dev
#eb deploy
eb create sproutup-dev -r us-west-2 -c sproutup-dev -t WebServer -i t2.small -s -k endurance
