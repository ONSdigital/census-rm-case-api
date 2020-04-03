# census-rm-case-api

# Overview
This case api service provides a range of Restful endpoints that -
* Retrieve case details by case id, case ref, UPRN or QID
* Retrieve a QID by case id
* Create and return a new Uac Qid Link 

The service relies on, and makes no changes to the casev2 schema maintained by census-rm-case-processor 

# Endpoints
## Case details:

* `GET /cases/uprn/<uprn>` (returns a list)
* `GET /cases/<case_id>`
* `GET /cases/qid/<qid>`
* `GET /cases/ref/<reference>`

All endpoints include an optional `caseevents` boolean query parameter (default = "false"), that can be used to specify that the JSON response includes an array of associated case events. For example:

* `GET /cases/<case_id>?caseevents=true`

If this query parameter is omitted these case events **will not** be returned with the case details. 

### Example Case JSON Response
```json
{
  "abpCode": "RD06",
  "addressLevel": "U",
  "addressLine1": "Flat 53 Francombe House",
  "addressLine2": "Commercial Road",
  "addressLine3": "",
  "caseEvents": [],
  "caseRef": "31283399",
  "addressType": "HH",
  "collectionExerciseId": "77c26716-5936-43e8-b56b-f5ca71765603",
  "createdDateTime": "2019-10-25T08:34:34.680556Z",
  "estabType": "Household",
  "id": "040f4608-d054-4ae9-b12f-1eee7e0fa284",
  "lad": "E06000023",
  "latitude": "51.4463421",
  "longitude": "-2.5924477",
  "lsoa": "E01014542",
  "msoa": "E02003043",
  "oa": "E00073438",
  "organisationName": "",
  "postcode": "XX1 0XX",
  "region": "E12000009",
  "surveyType": "CENSUS",
  "townName": "Windleybury",
  "uprn": "10008677190",
  "estabUprn": "103434302134"
}
```

## Create, return and link a new UAC QID pair for a case:

* `POST /uacqid/create`

### Example Request Rody
```json
{
"caseId": "820c9ebc-ac8c-483c-a9ec-0c2546d15d01",
"questionnaireType": "01"
}
```

### Example UAC QID JSON Response
```json
{
  "caseId": "820c9ebc-ac8c-483c-a9ec-0c2546d15d01",
  "qid": "0130000000000200",
  "uac": "f7hhksdgtk4vj59h"
}
```

* `GET /cases/<case_id>/qid` 
    Returns a newly generated QID/UAC pair every time based on the case type and links it to the case by eventual consistency (not synchronously)

### Example UAC QID JSON Response
```json
{
  "qid": "0130000000000200",
  "uac": "f7hhksdgtk4vj59h"
}
```

## CCS QID:

* `GET /cases/ccs/<case_id>/qid` 
    Returns the single assigned CCS telephone capture QID for a CCS case

### Example CCS QID JSON Response
```json
{
  "active": "True",
  "qid": "7130000000020300",
  "formType": "H"
}
```

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
