activator warn clean docker:stage
cp -R conf/docker/prod/. target/docker/
cd target/docker
eb init -r us-west-2 -p docker -k endurance sproutup
eb deploy prod
#eb create prod -r us-west-2 -c sproutup -t WebServer -i t2.small --scale 4 -k endurance
