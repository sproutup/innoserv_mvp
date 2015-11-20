repo = sproutupco
target = develop
cname = sproutup-co
environment_name = develop
configuration = develop
application_name = mvp

all: start

start:
	activator run

master:
	$(eval target := master)
	$(eval cname := master-sproutup-co) 
	$(eval environment_name := master)
	$(eval configuration := master) 
	$(eval application_name := master)

develop:
	$(eval target := develop)
	$(eval cname := develop-sproutup-co) 
	$(eval environment_name := develop)
	$(eval configuration := develop) 
	$(eval application_name := develop)

status:
	echo $(environment_name)/$(application_name)

deploy: clean stage prepare
	$(MAKE) -C target/docker $(target) deploy

create: clean stage prepare
	$(MAKE) -C target/docker $(target) create

recreate: clean stage prepare
	$(MAKE) -C target/docker $(target) recreate

build: clean stage prepare
	$(MAKE) -C target/docker build

run:
	$(MAKE) -C target/docker run

clean:
	activator clean

stage:
	activator warn docker:stage

init:
	eb init -r $(region) -p $(platform) -k $(keypair) $(environment_name)

prepare:
	cp -r conf/docker/. target/docker
	cp conf/$(environment_name).conf target/docker/files/opt/docker/conf/docker.conf

master1:
	cp -R conf/docker/prod/. target/docker/
	cd target/docker
	eb init -r us-west-2 -p docker -k endurance sproutup
	eb deploy prod

create-master:
	eb create prod -r us-west-2 -c sproutup -t WebServer -i t2.small --scale 4 -k endurance

create-develop:
	eb create develop -r us-west-2 -c develop-sproutup-co -t WebServer -i t2.small -s -k endurance

installmon:
	npm install -g browser-sync

dump:
	mysqldump -uroot -proot -h192.168.59.103 --no-create-info --ignore-table=sproutup_db.schema_version --ignore-table=sproutup_db.security_role --ignore-table=sproutup_db.numbers --ignore-table=sproutup_db.numbers_small --ignore-table=sproutup_db.date_dim --ignore-table=sproutup_db.metrics_dim --ignore-table=sproutup_db.provider_dim  sproutup_db > mydb.sql

mon:
	browser-sync start --proxy "0.0.0.0:9000" --files "**/*.js, **/*.java, **/*.html, **/*.less"

