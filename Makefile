# Set the container runtime based on architecture, default to docker for amd64 and podman for arm64
DOCKER ?= $(shell if [ "$$(uname -m)" = "arm64" ]; then echo podman; else echo docker; fi)

install:
	mvn clean install

build: install docker-build

build-no-test: install-no-test docker-build

install-no-test:
	mvn clean install -Dmaven.test.skip=true -Dexec.skip=true -Djacoco.skip=true

format:
	mvn spotless:apply

format-check:
	mvn spotless:check

check:
	mvn spotless:check pmd:check

test:
	CONTAINER_CLI=$(DOCKER) mvn clean verify jacoco:report

docker-build:
	$(DOCKER) build . --platform linux/amd64 -t census-rm-case-api:latest

rebuild-java-healthcheck:
	$(MAKE) -C src/test/resources/java_healthcheck rebuild-java-healthcheck
