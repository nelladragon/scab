# Copyright (C) 2017 Peter Robinson (peter@nelladragon.com)

# Build the SCAB gateway

# Usage:
#   [VERSION=v3] [REGISTRY="gcr.io/google_containers"] make build
VERSION?=v1
REGISTRY?=gcr.io/smart-contract-app-browser

release: clean build push clean

# builds a docker image that builds the app and packages it into a minimal docker image
build:
#	docker build --pull --rm --force-rm -t ${REGISTRY}/scab-gateway-builder .
	docker build -t ${REGISTRY}/scab-gateway-builder .
	docker run --rm ${REGISTRY}/scab-gateway-builder | docker build --pull -t "${REGISTRY}/scab-gateway:${VERSION}" -
#	docker run --rm ${REGISTRY}/guestbook-builder | docker build --pull -t "${REGISTRY}/guestbook:${VERSION}" -

# push the image to an registry
push:
	gcloud docker -- push ${REGISTRY}/scab-gateway:${VERSION}

# remove previous images and containers
clean:
	rm -f guestbook_bin
	docker rm -f ${REGISTRY}/guestbook-builder 2> /dev/null || true
	docker rmi -f ${REGISTRY}/guestbook-builder || true
	docker rmi -f "${REGISTRY}/guestbook:${VERSION}" || true

.PHONY: release clean build push
