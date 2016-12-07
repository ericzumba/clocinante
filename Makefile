IMAGE_NAME:=ericzumba/clocinante:latest

.PHONY: test
test:
	export $$(cat config/local.config) && lein midje

.PHONY: run
run: image 
	docker run \
		--net=host \
		-e CANO_HOST=api \
		-e CANO_PORT=8080 \
		-e TEST_HOST=api \
		-e TEST_PORT=8081 \
		-v $(SAMPLES_DIR):/samples \
		-t $(IMAGE_NAME) cat /samples/sample.urls | lein midje

.PHONY: image
image:
	docker build -t $(IMAGE_NAME) .
