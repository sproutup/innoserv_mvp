activator warn clean docker:stage
cp ~/workspace/sproutup_mvp/conf/docker/Dockerfile.dev ~/workspace/sproutup_mvp/target/docker/Dockerfile
sudo docker build -t sproutup/play ~/workspace/sproutup_mvp/target/docker
sudo docker stop play || true
sudo docker rm play || true
sudo docker run -d -p 9000:9000 --link mysql:mysql --add-host=dockerhost:172.17.42.1 --name play sproutup/play

