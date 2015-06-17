all: run

run:
	activator run

installmon:
	npm install -g browser-sync

mon:
	browser-sync start --proxy "0.0.0.0:9000" --files "**/*.js, **/*.java, **/*.html"
