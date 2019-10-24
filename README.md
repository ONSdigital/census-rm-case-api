# census-rm-case-api

# Overview
This case api service provides a range of Restful endpoints that -
* Retrieve case details by case id, case ref, UPRN or QID
* Retrieve a QID by case id
* Create and return a new Uac Qid Link - this resulting 

The service relies on, and makes no changes to the casev2 schema maintained by census-rm-case-processor 

# Queues
The creation of a Uac Qid Link emits a RM_UAC_CREATED message to the uac-qid-created-exchange

# Configuration

By default settings in src/main/resources/application.yml are used to configure [census-rm-case-api](https://github.com/ONSdigital/census-rm-case-api)

For production the configuration is overridden by the K8S apply script

# How to run
The service requires several other services to be running started from census-rm-docker-dev

# How to debug census-rm-case-api locally 
 
## Running as a docker image 
* Start census-rm-docker-dev services with the following line in section caseapi | environment in rm-services.yml
*       - JAVA_OPTS=-Xmx512m -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8162
* In IntelliJ, create a "Remote" configuration, set port = 8162 and run in debug mode

## Running inside IntelliJ
* Stop the census-rm-case-api service if already running
* In IntelliJ, create a SpringBoot Run configuration and run in debug mode

# Testing
## In isolation
From the project root directory, run "mvn clean install", this -  
* Runs all unit tests
* Builds a new local docker image
* Brings up this image with all required services and runs all integration tests

## With Acceptance Tests
* From census-rm-acceptance-tests, run "make test"
