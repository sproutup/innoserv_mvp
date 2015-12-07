environment_name = develop
platform = docker
application_name = mvp
region = us-west-2
keypair = endurance
configuration = mvp
domain = sproutup-co
repo = sproutupco

.PHONY: all master develop

all: create

master:
	$(eval environment_name := master)

develop:
	$(eval environment_name := develop)

deploy: init
	eb deploy $(application_name)-$(environment_name)

init: prepare
	eb init -r $(region) -p $(platform) -k $(keypair) $(environment_name)

prepare:
	sed s/#tag#/$(environment_name)/ Dockerrun.aws.template >Dockerrun.aws.json

recreate: terminate create

create: init config-put
	eb create $(application_name)-$(environment_name) -c $(application_name)-$(environment_name)-$(domain) --cfg $(configuration)-$(environment_name)

terminate: init
	eb terminate $(application_name)-$(environment_name) --force

config-save:
	eb config save $(configuration) --cfg $(configuration)

config: init config-put
	eb config $(application_name)-$(environment_name) --cfg $(application_name)-$(environment_name)

config-put: init
	eb config put $(application_name)-$(environment_name)
