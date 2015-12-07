environment_name = develop
platform = docker
application_name = mvp
region = us-west-2
keypair = endurance
configuration = mvp
domain = sproutup-co
repo = sproutupco

.PHONY: all master develop

all: build

master:
	$(eval environment_name := master)

develop:
	$(eval environment_name := develop)

build:
	docker build -t $(repo)/$(application_name):$(environment_name) .

push: build
	docker push $(repo)/$(application_name):$(environment_name)

rebuild: stop delete build run

stop:
	docker stop $(repo)/$(application_name):$(environment_name)

restart: stop start

start:
	docker start $(repo)/$(application_name):$(environment_name)

run:
	docker run -p 9000:9000 -e AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY --net="host" -d  $(repo)/$(application_name):$(environment_name)

runia:
	docker run -p 9000:9000 -e AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY --net="host" -it --rm $(repo)/$(application_name):$(environment_name) /bin/sh

delete: init
	docker rm $(application_name)

node:
	gulp

deploy: push
	$(MAKE) -C target $(environment_name) deploy

create: push
	$(MAKE) -C target $(environment_name) create

recreate: push
	$(MAKE) -C target $(environment_name) recreate

