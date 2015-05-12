activator warn clean docker:stage
cp conf/docker/master/Dockerfile target/docker/Dockerfile
cd target/docker
eb init -r us-west-2 -p docker -k endurance dev
eb deploy
#eb create sproutup-dev -r us-west-2 -c sproutup-dev -t WebServer -i t2.small -s -k endurance
