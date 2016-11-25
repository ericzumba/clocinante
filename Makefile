.PHONY: test
test:
	export $$(cat config/local.config) && lein midje
