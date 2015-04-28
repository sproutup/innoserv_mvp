activator warn clean docker:stage
cp ~/workspace/sproutup_mvp/conf/docker/master/Dockerfile ~/workspace/sproutup_mvp/target/docker/Dockerfile
cd target/docker
eb init -r us-west-2 -p docker -k endurance dev
eb deploy
#eb create sproutup-dev -r us-west-2 -c sproutup-dev -t WebServer -i t1.small -s -k endurance
