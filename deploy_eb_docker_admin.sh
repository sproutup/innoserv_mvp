activator warn clean docker:stage
cp -R ~/workspace/sproutup_mvp/conf/docker/admin/. ~/workspace/sproutup_mvp/target/docker/
cd target/docker
eb init -r us-west-2 -p docker -k endurance sproutup
eb deploy admin
#eb create admin -r us-west-2 -c sproutup-admin -t WebServer -i t2.small -s -k endurance
