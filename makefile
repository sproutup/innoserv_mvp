all: run

run:
	activator run

develop:
	$(eval cname := master-sproutup-co) 
	$(eval environment_name := master)
	$(eval configuration := master) 
	$(eval application_name := master)

master:
	$(eval cname := develop-sproutup-co) 
	$(eval environment_name := develop)
	$(eval configuration := develop) 
	$(eval application_name := develop)

status:
	echo $(environment_name)/$(application_name)

clean:
	acivator clean

stage:
	activator warn docker:stage

init:
	eb init -r $(region) -p $(platform) -k $(keypair) $(environment_name)

create: init
	eb create $(application_name) -c $(cname) --cfg $(configuration)

develop:
	cp conf/docker/master/Dockerfile target/docker/Dockerfile
	cd target/docker
	eb init -r us-west-2 -p docker -k endurance dev
	eb deploy

master:
	cp -R conf/docker/prod/. target/docker/
	cd target/docker
	eb init -r us-west-2 -p docker -k endurance sproutup
	eb deploy prod

create-master:
	eb create prod -r us-west-2 -c sproutup -t WebServer -i t2.small --scale 4 -k endurance

create-develop:
	eb create sproutup-dev -r us-west-2 -c sproutup-dev -t WebServer -i t2.small -s -k endurance

installmon:
	npm install -g browser-sync

mon:
	browser-sync start --proxy "0.0.0.0:9000" --files "**/*.js, **/*.java, **/*.html"

