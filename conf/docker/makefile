environment_name = develop
platform = docker
application_name = mvp
region = us-west-2
keypair = endurance
configuration = mvp
domain = sproutup-co
repo = sproutupco

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
	docker run -d -p 3000:3000 --name $(application_name) --net="host" --env-file local-env.list $(application_name)

runia:
	docker run -it -p 3000:3020 --env-file local-env.list --net="host" -t $(repo)/$(application_name) /bin/sh

delete: init
	docker rm $(application_name)

node:
	gulp

deploy: push
	$(MAKE) -C target $(target) deploy

