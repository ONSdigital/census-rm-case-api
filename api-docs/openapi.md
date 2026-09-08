---
title: Case API Service v1
language_tabs:
  - shell: Shell
  - http: HTTP
  - javascript: JavaScript
  - ruby: Ruby
  - python: Python
  - php: PHP
  - java: Java
  - go: Go
toc_footers: []
includes: []
search: true
highlight_theme: darkula
headingLevel: 2

---

<!-- Generator: Widdershins v4.0.1 -->

<h1 id="case-api-service">Case API Service v1</h1>

> Scroll down for code samples, example requests and responses. Select a language for code samples from the tabs above or the mobile navigation menu.

RESTful API for managing census case data. Provides endpoints for querying and retrieving case information by various identifiers (UUID, reference, UPRN, postcode, QID). Returns comprehensive case details including address information, case status, and associated events.

Base URLs:

* <a href="http://localhost:8161">http://localhost:8161</a>

License: <a href="https://www.ons.gov.uk">Office for National Statistics</a>

<h1 id="case-api-service-qid-endpoint">QID Endpoint</h1>

Services for retrieving and managing questionnaire ID (QID) and UAC link information

## putQidLinkToCase

<a id="opIdputQidLinkToCase"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8161/qids/link \
  -H 'Content-Type: application/json'

```

```http
PUT http://localhost:8161/qids/link HTTP/1.1
Host: localhost:8161
Content-Type: application/json

```

```javascript
const inputBody = '{
  "transactionId": "a11e3456-e89b-12d3-a456-426614174000",
  "channel": "CONTACT_CENTRE",
  "qidLink": {
    "questionnaireId": "Q123456",
    "caseId": "a11e3456-e89b-12d3-a456-426614174000"
  }
}';
const headers = {
  'Content-Type':'application/json'
};

fetch('http://localhost:8161/qids/link',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json'
}

result = RestClient.put 'http://localhost:8161/qids/link',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json'
}

r = requests.put('http://localhost:8161/qids/link', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('PUT','http://localhost:8161/qids/link', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://localhost:8161/qids/link");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("PUT");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("PUT", "http://localhost:8161/qids/link", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`PUT /qids/link`

*Link QID to case*

Links a questionnaire ID to a case. This endpoint is currently not implemented as the subscription for questionnaire links is not available.

> Body parameter

```json
{
  "transactionId": "a11e3456-e89b-12d3-a456-426614174000",
  "channel": "CONTACT_CENTRE",
  "qidLink": {
    "questionnaireId": "Q123456",
    "caseId": "a11e3456-e89b-12d3-a456-426614174000"
  }
}
```

<h3 id="putqidlinktocase-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[NewQidLink](#schemanewqidlink)|true|QID link information to be created|

<h3 id="putqidlinktocase-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|501|[Not Implemented](https://tools.ietf.org/html/rfc7231#section-6.6.2)|Not Implemented - Questionnaire link subscription not available|None|

<aside class="success">
This operation does not require authentication
</aside>

## getUacQidLinkByQid

<a id="opIdgetUacQidLinkByQid"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8161/qids/{qid} \
  -H 'Accept: application/json'

```

```http
GET http://localhost:8161/qids/{qid} HTTP/1.1
Host: localhost:8161
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json'
};

fetch('http://localhost:8161/qids/{qid}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json'
}

result = RestClient.get 'http://localhost:8161/qids/{qid}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json'
}

r = requests.get('http://localhost:8161/qids/{qid}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://localhost:8161/qids/{qid}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://localhost:8161/qids/{qid}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://localhost:8161/qids/{qid}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /qids/{qid}`

*Retrieve QID link details*

Retrieves the UAC-QID link information for a specified questionnaire ID, including associated case ID if available.

<h3 id="getuacqidlinkbyqid-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|qid|path|string|true|Questionnaire Identifier (QID) to retrieve|

> Example responses

> 200 Response

```json
{
  "questionnaireId": "Q123456",
  "caseId": "a11e3456-e89b-12d3-a456-426614174000"
}
```

<h3 id="getuacqidlinkbyqid-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|QID link details retrieved successfully|[QidLink](#schemaqidlink)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Questionnaire ID not found|None|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal server error occurred|None|

<aside class="success">
This operation does not require authentication
</aside>

<h1 id="case-api-service-case-endpoint">Case Endpoint</h1>

Services for querying and retrieving census cases

## getAllCaseDetailsByCaseId

<a id="opIdgetAllCaseDetailsByCaseId"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8161/cases/case-details/{caseId} \
  -H 'Accept: application/json'

```

```http
GET http://localhost:8161/cases/case-details/{caseId} HTTP/1.1
Host: localhost:8161
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json'
};

fetch('http://localhost:8161/cases/case-details/{caseId}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json'
}

result = RestClient.get 'http://localhost:8161/cases/case-details/{caseId}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json'
}

r = requests.get('http://localhost:8161/cases/case-details/{caseId}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://localhost:8161/cases/case-details/{caseId}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://localhost:8161/cases/case-details/{caseId}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://localhost:8161/cases/case-details/{caseId}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /cases/case-details/{caseId}`

*Get full case details by Case ID*

Retrieves complete detailed case attributes for a given case UUID.

<h3 id="getallcasedetailsbycaseid-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|caseId|path|string(uuid)|true|Unique UUID of the case|

> Example responses

> 200 Response

```json
{
  "id": "a11e3456-e89b-12d3-a456-426614174000",
  "caseRef": 100000000000001,
  "uprn": "10008677190",
  "estabUprn": "10008677190",
  "caseType": "HH",
  "addressType": "HH",
  "estabType": "HOUSEHOLD",
  "addressLevel": "U",
  "abpCode": "RD06",
  "organisationName": "Acme Corporation",
  "addressLine1": "Flat 51 Francombe House",
  "addressLine2": "Commercial Road",
  "addressLine3": "Suite 3",
  "townName": "Windleybury",
  "postcode": "XX1 0XX",
  "latitude": "51.5074",
  "longitude": "-0.1278",
  "oa": "E00073438",
  "lsoa": "E01014540",
  "msoa": "E02003043",
  "lad": "E06000023",
  "region": "E12000009",
  "htcWillingness": "3",
  "htcDigital": "4",
  "fieldCoordinatorId": "FC12344",
  "fieldOfficerId": "FO12345",
  "treatmentCode": "HH_PSCE",
  "ceExpectedCapacity": 1505,
  "ceActualResponses": 1504,
  "collectionExerciseId": "b22e3456-e89b-12d3-a456-426614174000",
  "createdDateTime": "2024-01-15T10:30:00Z",
  "events": [],
  "receiptReceived": true,
  "refusalReceived": "SOFT_REFUSAL",
  "invalid": false,
  "lastUpdated": "2024-01-20T14:45:00Z",
  "printBatch": "15",
  "surveyLaunched": true
}
```

<h3 id="getallcasedetailsbycaseid-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Detailed case record retrieved successfully|[CaseDetailsDTO](#schemacasedetailsdto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Case ID not found|None|

<aside class="success">
This operation does not require authentication
</aside>

## getCasesByPostcode

<a id="opIdgetCasesByPostcode"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8161/cases/postcode/{postcode} \
  -H 'Accept: application/json'

```

```http
GET http://localhost:8161/cases/postcode/{postcode} HTTP/1.1
Host: localhost:8161
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json'
};

fetch('http://localhost:8161/cases/postcode/{postcode}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json'
}

result = RestClient.get 'http://localhost:8161/cases/postcode/{postcode}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json'
}

r = requests.get('http://localhost:8161/cases/postcode/{postcode}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://localhost:8161/cases/postcode/{postcode}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://localhost:8161/cases/postcode/{postcode}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://localhost:8161/cases/postcode/{postcode}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /cases/postcode/{postcode}`

*Find cases by Postcode*

Retrieves all cases located within the specified postcode area.

<h3 id="getcasesbypostcode-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|postcode|path|string|true|Postal code identifier|

> Example responses

> 200 Response

```json
[
  {
    "caseRef": "100000000000001",
    "id": "a11e3456-e89b-12d3-a456-426614174000",
    "estabType": "HOUSEHOLD",
    "uprn": "10008677190",
    "estabUprn": "10008677190",
    "collectionExerciseId": "b22e3456-e89b-12d3-a456-426614174000",
    "surveyType": "CENSUS",
    "addressType": "HH",
    "caseType": "HH",
    "createdDateTime": "2024-01-15T10:30:00Z",
    "addressLine1": "Flat 51 Francombe House",
    "addressLine2": "Commercial Road",
    "addressLine3": "Suite 3",
    "townName": "Windleybury",
    "postcode": "XX1 0XX",
    "organisationName": "Acme Corporation",
    "addressLevel": "U",
    "abpCode": "RD06",
    "region": "E12000007",
    "latitude": "51.5074",
    "longitude": "-0.1278",
    "oa": "E00073438",
    "lsoa": "E01014540",
    "msoa": "E02003043",
    "lad": "E06000023",
    "lastUpdated": "2024-01-20T14:45:00Z",
    "caseEvents": [],
    "secureEstablishment": false,
    "invalid": false
  }
]
```

<h3 id="getcasesbypostcode-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Matching cases retrieved successfully|Inline|

<h3 id="getcasesbypostcode-responseschema">Response Schema</h3>

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[[CaseContainerDTO](#schemacasecontainerdto)]|false|none|[Data Transfer Object representing a census case container]|
|» abpCode|string|false|none|AddressBase classification code|
|» addressLevel|string|false|none|Address level classification (e.g., E, U (signifying Establishment and Unit))|
|» addressLine1|string|false|none|First address line|
|» addressLine2|string|false|none|Second address line|
|» addressLine3|string|false|none|Third address line|
|» addressType|string|false|none|Residential address frame type (e.g., HH, CE)|
|» caseEvents|[[CaseEventDTO](#schemacaseeventdto)]|false|none|List of events associated with the case|
|»» createdDateTime|string(date-time)|true|none|Date and time when the event was created|
|»» description|string|false|none|Human-readable description of the event|
|»» eventType|[EventTypeDTO](#schemaeventtypedto)|true|none|Enumeration of all possible event types that can occur in the census RM system|
|»» id|string(uuid)|true|none|Unique event identifier (UUID)|
|» caseRef|string|true|none|Unique numeric reference for the case|
|» caseType|string|false|none|Case type classification (e.g., HH, HI, CE). It will match addressType unless it is an individual (HI) case.|
|» collectionExerciseId|string(uuid)|false|none|Collection Exercise UUID identifier|
|» createdDateTime|string(date-time)|false|none|Date and time when the case was created|
|» estabType|string|false|none|Establishment type (e.g., HALL OF RESIDENCE, HOUSEHOLD, SHELTERED ACCOMMODATION, RESIDENTIAL CARAVAN, RESIDENTIAL BOAT)|
|» estabUprn|string|false|none|Establishment UPRN for non-household establishments|
|» id|string(uuid)|true|none|Unique case UUID identifier|
|» invalid|boolean|false|none|Flag indicating if the case record is marked as invalid|
|» lad|string|false|none|Local Authority District code (e.g., N06000023, S06000023, E06000023, W06000023)|
|» lastUpdated|string(date-time)|false|none|Date and time when the case was last updated|
|» latitude|string|false|none|Geographic latitude coordinate|
|» longitude|string|false|none|Geographic longitude coordinate|
|» lsoa|string|false|none|Lower Layer Super Output Area grid reference (e.g., N01014540, S01014540, E01014540, W01014540)|
|» msoa|string|false|none|Middle Layer Super Output Area grid reference (e.g., N02003043, S02003043, E02003043, W02003043)|
|» oa|string|false|none|Output Area grid reference (e.g., N00073438, S00073438, E00073438, W00073438)|
|» organisationName|string|false|none|Name of the organisation at the address|
|» postcode|string|false|none|UK postal code|
|» region|string|false|none|Administrative region code (e.g., N12000007, S12000007, E12000007, W12000007)|
|» secureEstablishment|boolean|false|none|Indicator whether address is a secure establishment|
|» surveyType|string|false|none|Type of survey (e.g., CENSUS, CCS)|
|» townName|string|false|none|Town or city name|
|» uprn|string|false|none|Unique Property Reference Number|

#### Enumerated Values

|Property|Value|
|---|---|
|eventType|NEW_CASE|
|eventType|RECEIPT|
|eventType|REFUSAL|
|eventType|INVALID_CASE|
|eventType|EQ_LAUNCH|
|eventType|UAC_AUTHENTICATION|
|eventType|PRINT_FULFILMENT|
|eventType|EXPORT_FILE|
|eventType|DEACTIVATE_UAC|
|eventType|UPDATE_SAMPLE|
|eventType|UPDATE_SAMPLE_SENSITIVE|
|eventType|SMS_FULFILMENT|
|eventType|ACTION_RULE_SMS_REQUEST|
|eventType|EMAIL_FULFILMENT|
|eventType|ACTION_RULE_EMAIL_REQUEST|
|eventType|ACTION_RULE_SMS_CONFIRMATION|
|eventType|ACTION_RULE_EMAIL_CONFIRMATION|
|eventType|ERASE_DATA|

<aside class="success">
This operation does not require authentication
</aside>

## findCaseByQid

<a id="opIdfindCaseByQid"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8161/cases/qid/{qid} \
  -H 'Accept: application/json'

```

```http
GET http://localhost:8161/cases/qid/{qid} HTTP/1.1
Host: localhost:8161
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json'
};

fetch('http://localhost:8161/cases/qid/{qid}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json'
}

result = RestClient.get 'http://localhost:8161/cases/qid/{qid}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json'
}

r = requests.get('http://localhost:8161/cases/qid/{qid}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://localhost:8161/cases/qid/{qid}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://localhost:8161/cases/qid/{qid}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://localhost:8161/cases/qid/{qid}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /cases/qid/{qid}`

*Find case by Questionnaire ID (QID)*

Retrieves minimal case details linked to a specific questionnaire ID.

<h3 id="findcasebyqid-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|qid|path|string|true|Questionnaire Identifier|

> Example responses

> 200 Response

```json
{
  "caseRef": "100000000000001",
  "id": "a11e3456-e89b-12d3-a456-426614174000",
  "estabType": "HOUSEHOLD",
  "uprn": "10008677190",
  "estabUprn": "10008677190",
  "collectionExerciseId": "b22e3456-e89b-12d3-a456-426614174000",
  "surveyType": "CENSUS",
  "addressType": "HH",
  "caseType": "HH",
  "createdDateTime": "2024-01-15T10:30:00Z",
  "addressLine1": "Flat 51 Francombe House",
  "addressLine2": "Commercial Road",
  "addressLine3": "Suite 3",
  "townName": "Windleybury",
  "postcode": "XX1 0XX",
  "organisationName": "Acme Corporation",
  "addressLevel": "U",
  "abpCode": "RD06",
  "region": "E12000007",
  "latitude": "51.5074",
  "longitude": "-0.1278",
  "oa": "E00073438",
  "lsoa": "E01014540",
  "msoa": "E02003043",
  "lad": "E06000023",
  "lastUpdated": "2024-01-20T14:45:00Z",
  "caseEvents": [],
  "secureEstablishment": false,
  "invalid": false
}
```

<h3 id="findcasebyqid-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Case retrieved successfully|[CaseContainerDTO](#schemacasecontainerdto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|QID not found|None|

<aside class="success">
This operation does not require authentication
</aside>

## findCaseByReference

<a id="opIdfindCaseByReference"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8161/cases/ref/{reference} \
  -H 'Accept: application/json'

```

```http
GET http://localhost:8161/cases/ref/{reference} HTTP/1.1
Host: localhost:8161
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json'
};

fetch('http://localhost:8161/cases/ref/{reference}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json'
}

result = RestClient.get 'http://localhost:8161/cases/ref/{reference}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json'
}

r = requests.get('http://localhost:8161/cases/ref/{reference}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://localhost:8161/cases/ref/{reference}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://localhost:8161/cases/ref/{reference}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://localhost:8161/cases/ref/{reference}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /cases/ref/{reference}`

*Find case by Reference*

Retrieves a single case container record using the numeric case reference.

<h3 id="findcasebyreference-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|reference|path|integer(int64)|true|Unique numeric case reference identifier|
|caseEvents|query|boolean|false|Flag indicating whether to include case events|

> Example responses

> 200 Response

```json
{
  "caseRef": "100000000000001",
  "id": "a11e3456-e89b-12d3-a456-426614174000",
  "estabType": "HOUSEHOLD",
  "uprn": "10008677190",
  "estabUprn": "10008677190",
  "collectionExerciseId": "b22e3456-e89b-12d3-a456-426614174000",
  "surveyType": "CENSUS",
  "addressType": "HH",
  "caseType": "HH",
  "createdDateTime": "2024-01-15T10:30:00Z",
  "addressLine1": "Flat 51 Francombe House",
  "addressLine2": "Commercial Road",
  "addressLine3": "Suite 3",
  "townName": "Windleybury",
  "postcode": "XX1 0XX",
  "organisationName": "Acme Corporation",
  "addressLevel": "U",
  "abpCode": "RD06",
  "region": "E12000007",
  "latitude": "51.5074",
  "longitude": "-0.1278",
  "oa": "E00073438",
  "lsoa": "E01014540",
  "msoa": "E02003043",
  "lad": "E06000023",
  "lastUpdated": "2024-01-20T14:45:00Z",
  "caseEvents": [],
  "secureEstablishment": false,
  "invalid": false
}
```

<h3 id="findcasebyreference-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Case record found successfully|[CaseContainerDTO](#schemacasecontainerdto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Case reference not found|None|

<aside class="success">
This operation does not require authentication
</aside>

## findCasesByUPRN

<a id="opIdfindCasesByUPRN"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8161/cases/uprn/{uprn} \
  -H 'Accept: application/json'

```

```http
GET http://localhost:8161/cases/uprn/{uprn} HTTP/1.1
Host: localhost:8161
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json'
};

fetch('http://localhost:8161/cases/uprn/{uprn}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json'
}

result = RestClient.get 'http://localhost:8161/cases/uprn/{uprn}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json'
}

r = requests.get('http://localhost:8161/cases/uprn/{uprn}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://localhost:8161/cases/uprn/{uprn}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://localhost:8161/cases/uprn/{uprn}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://localhost:8161/cases/uprn/{uprn}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /cases/uprn/{uprn}`

*Find cases by UPRN*

Retrieves all cases associated with a Unique Property Reference Number.

<h3 id="findcasesbyuprn-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|uprn|path|string|true|Unique Property Reference Number|
|caseEvents|query|boolean|false|Flag indicating whether to include case events|
|validAddressOnly|query|boolean|false|Filter results to valid addresses only|

> Example responses

> 200 Response

```json
[
  {
    "caseRef": "100000000000001",
    "id": "a11e3456-e89b-12d3-a456-426614174000",
    "estabType": "HOUSEHOLD",
    "uprn": "10008677190",
    "estabUprn": "10008677190",
    "collectionExerciseId": "b22e3456-e89b-12d3-a456-426614174000",
    "surveyType": "CENSUS",
    "addressType": "HH",
    "caseType": "HH",
    "createdDateTime": "2024-01-15T10:30:00Z",
    "addressLine1": "Flat 51 Francombe House",
    "addressLine2": "Commercial Road",
    "addressLine3": "Suite 3",
    "townName": "Windleybury",
    "postcode": "XX1 0XX",
    "organisationName": "Acme Corporation",
    "addressLevel": "U",
    "abpCode": "RD06",
    "region": "E12000007",
    "latitude": "51.5074",
    "longitude": "-0.1278",
    "oa": "E00073438",
    "lsoa": "E01014540",
    "msoa": "E02003043",
    "lad": "E06000023",
    "lastUpdated": "2024-01-20T14:45:00Z",
    "caseEvents": [],
    "secureEstablishment": false,
    "invalid": false
  }
]
```

<h3 id="findcasesbyuprn-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Matching cases retrieved successfully|Inline|

<h3 id="findcasesbyuprn-responseschema">Response Schema</h3>

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[[CaseContainerDTO](#schemacasecontainerdto)]|false|none|[Data Transfer Object representing a census case container]|
|» abpCode|string|false|none|AddressBase classification code|
|» addressLevel|string|false|none|Address level classification (e.g., E, U (signifying Establishment and Unit))|
|» addressLine1|string|false|none|First address line|
|» addressLine2|string|false|none|Second address line|
|» addressLine3|string|false|none|Third address line|
|» addressType|string|false|none|Residential address frame type (e.g., HH, CE)|
|» caseEvents|[[CaseEventDTO](#schemacaseeventdto)]|false|none|List of events associated with the case|
|»» createdDateTime|string(date-time)|true|none|Date and time when the event was created|
|»» description|string|false|none|Human-readable description of the event|
|»» eventType|[EventTypeDTO](#schemaeventtypedto)|true|none|Enumeration of all possible event types that can occur in the census RM system|
|»» id|string(uuid)|true|none|Unique event identifier (UUID)|
|» caseRef|string|true|none|Unique numeric reference for the case|
|» caseType|string|false|none|Case type classification (e.g., HH, HI, CE). It will match addressType unless it is an individual (HI) case.|
|» collectionExerciseId|string(uuid)|false|none|Collection Exercise UUID identifier|
|» createdDateTime|string(date-time)|false|none|Date and time when the case was created|
|» estabType|string|false|none|Establishment type (e.g., HALL OF RESIDENCE, HOUSEHOLD, SHELTERED ACCOMMODATION, RESIDENTIAL CARAVAN, RESIDENTIAL BOAT)|
|» estabUprn|string|false|none|Establishment UPRN for non-household establishments|
|» id|string(uuid)|true|none|Unique case UUID identifier|
|» invalid|boolean|false|none|Flag indicating if the case record is marked as invalid|
|» lad|string|false|none|Local Authority District code (e.g., N06000023, S06000023, E06000023, W06000023)|
|» lastUpdated|string(date-time)|false|none|Date and time when the case was last updated|
|» latitude|string|false|none|Geographic latitude coordinate|
|» longitude|string|false|none|Geographic longitude coordinate|
|» lsoa|string|false|none|Lower Layer Super Output Area grid reference (e.g., N01014540, S01014540, E01014540, W01014540)|
|» msoa|string|false|none|Middle Layer Super Output Area grid reference (e.g., N02003043, S02003043, E02003043, W02003043)|
|» oa|string|false|none|Output Area grid reference (e.g., N00073438, S00073438, E00073438, W00073438)|
|» organisationName|string|false|none|Name of the organisation at the address|
|» postcode|string|false|none|UK postal code|
|» region|string|false|none|Administrative region code (e.g., N12000007, S12000007, E12000007, W12000007)|
|» secureEstablishment|boolean|false|none|Indicator whether address is a secure establishment|
|» surveyType|string|false|none|Type of survey (e.g., CENSUS, CCS)|
|» townName|string|false|none|Town or city name|
|» uprn|string|false|none|Unique Property Reference Number|

#### Enumerated Values

|Property|Value|
|---|---|
|eventType|NEW_CASE|
|eventType|RECEIPT|
|eventType|REFUSAL|
|eventType|INVALID_CASE|
|eventType|EQ_LAUNCH|
|eventType|UAC_AUTHENTICATION|
|eventType|PRINT_FULFILMENT|
|eventType|EXPORT_FILE|
|eventType|DEACTIVATE_UAC|
|eventType|UPDATE_SAMPLE|
|eventType|UPDATE_SAMPLE_SENSITIVE|
|eventType|SMS_FULFILMENT|
|eventType|ACTION_RULE_SMS_REQUEST|
|eventType|EMAIL_FULFILMENT|
|eventType|ACTION_RULE_EMAIL_REQUEST|
|eventType|ACTION_RULE_SMS_CONFIRMATION|
|eventType|ACTION_RULE_EMAIL_CONFIRMATION|
|eventType|ERASE_DATA|

<aside class="success">
This operation does not require authentication
</aside>

## findCaseById

<a id="opIdfindCaseById"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8161/cases/{id} \
  -H 'Accept: application/json'

```

```http
GET http://localhost:8161/cases/{id} HTTP/1.1
Host: localhost:8161
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json'
};

fetch('http://localhost:8161/cases/{id}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json'
}

result = RestClient.get 'http://localhost:8161/cases/{id}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json'
}

r = requests.get('http://localhost:8161/cases/{id}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://localhost:8161/cases/{id}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://localhost:8161/cases/{id}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://localhost:8161/cases/{id}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /cases/{id}`

*Find case by ID*

Retrieves a single case container record matching the specified UUID.

<h3 id="findcasebyid-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|string(uuid)|true|Unique UUID of the case|
|caseEvents|query|boolean|false|Flag indicating whether to include case events|

> Example responses

> 200 Response

```json
{
  "caseRef": "100000000000001",
  "id": "a11e3456-e89b-12d3-a456-426614174000",
  "estabType": "HOUSEHOLD",
  "uprn": "10008677190",
  "estabUprn": "10008677190",
  "collectionExerciseId": "b22e3456-e89b-12d3-a456-426614174000",
  "surveyType": "CENSUS",
  "addressType": "HH",
  "caseType": "HH",
  "createdDateTime": "2024-01-15T10:30:00Z",
  "addressLine1": "Flat 51 Francombe House",
  "addressLine2": "Commercial Road",
  "addressLine3": "Suite 3",
  "townName": "Windleybury",
  "postcode": "XX1 0XX",
  "organisationName": "Acme Corporation",
  "addressLevel": "U",
  "abpCode": "RD06",
  "region": "E12000007",
  "latitude": "51.5074",
  "longitude": "-0.1278",
  "oa": "E00073438",
  "lsoa": "E01014540",
  "msoa": "E02003043",
  "lad": "E06000023",
  "lastUpdated": "2024-01-20T14:45:00Z",
  "caseEvents": [],
  "secureEstablishment": false,
  "invalid": false
}
```

<h3 id="findcasebyid-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Case record found successfully|[CaseContainerDTO](#schemacasecontainerdto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Case record not found|None|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal server error occurred|None|

<aside class="success">
This operation does not require authentication
</aside>

# Schemas

<h2 id="tocS_CaseContainerDTO">CaseContainerDTO</h2>
<!-- backwards compatibility -->
<a id="schemacasecontainerdto"></a>
<a id="schema_CaseContainerDTO"></a>
<a id="tocScasecontainerdto"></a>
<a id="tocscasecontainerdto"></a>

```json
{
  "caseRef": "100000000000001",
  "id": "a11e3456-e89b-12d3-a456-426614174000",
  "estabType": "HOUSEHOLD",
  "uprn": "10008677190",
  "estabUprn": "10008677190",
  "collectionExerciseId": "b22e3456-e89b-12d3-a456-426614174000",
  "surveyType": "CENSUS",
  "addressType": "HH",
  "caseType": "HH",
  "createdDateTime": "2024-01-15T10:30:00Z",
  "addressLine1": "Flat 51 Francombe House",
  "addressLine2": "Commercial Road",
  "addressLine3": "Suite 3",
  "townName": "Windleybury",
  "postcode": "XX1 0XX",
  "organisationName": "Acme Corporation",
  "addressLevel": "U",
  "abpCode": "RD06",
  "region": "E12000007",
  "latitude": "51.5074",
  "longitude": "-0.1278",
  "oa": "E00073438",
  "lsoa": "E01014540",
  "msoa": "E02003043",
  "lad": "E06000023",
  "lastUpdated": "2024-01-20T14:45:00Z",
  "caseEvents": [],
  "secureEstablishment": false,
  "invalid": false
}

```

Data Transfer Object representing a census case container

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|abpCode|string|false|none|AddressBase classification code|
|addressLevel|string|false|none|Address level classification (e.g., E, U (signifying Establishment and Unit))|
|addressLine1|string|false|none|First address line|
|addressLine2|string|false|none|Second address line|
|addressLine3|string|false|none|Third address line|
|addressType|string|false|none|Residential address frame type (e.g., HH, CE)|
|caseEvents|[[CaseEventDTO](#schemacaseeventdto)]|false|none|List of events associated with the case|
|caseRef|string|true|none|Unique numeric reference for the case|
|caseType|string|false|none|Case type classification (e.g., HH, HI, CE). It will match addressType unless it is an individual (HI) case.|
|collectionExerciseId|string(uuid)|false|none|Collection Exercise UUID identifier|
|createdDateTime|string(date-time)|false|none|Date and time when the case was created|
|estabType|string|false|none|Establishment type (e.g., HALL OF RESIDENCE, HOUSEHOLD, SHELTERED ACCOMMODATION, RESIDENTIAL CARAVAN, RESIDENTIAL BOAT)|
|estabUprn|string|false|none|Establishment UPRN for non-household establishments|
|id|string(uuid)|true|none|Unique case UUID identifier|
|invalid|boolean|false|none|Flag indicating if the case record is marked as invalid|
|lad|string|false|none|Local Authority District code (e.g., N06000023, S06000023, E06000023, W06000023)|
|lastUpdated|string(date-time)|false|none|Date and time when the case was last updated|
|latitude|string|false|none|Geographic latitude coordinate|
|longitude|string|false|none|Geographic longitude coordinate|
|lsoa|string|false|none|Lower Layer Super Output Area grid reference (e.g., N01014540, S01014540, E01014540, W01014540)|
|msoa|string|false|none|Middle Layer Super Output Area grid reference (e.g., N02003043, S02003043, E02003043, W02003043)|
|oa|string|false|none|Output Area grid reference (e.g., N00073438, S00073438, E00073438, W00073438)|
|organisationName|string|false|none|Name of the organisation at the address|
|postcode|string|false|none|UK postal code|
|region|string|false|none|Administrative region code (e.g., N12000007, S12000007, E12000007, W12000007)|
|secureEstablishment|boolean|false|none|Indicator whether address is a secure establishment|
|surveyType|string|false|none|Type of survey (e.g., CENSUS, CCS)|
|townName|string|false|none|Town or city name|
|uprn|string|false|none|Unique Property Reference Number|

<h2 id="tocS_CaseDetailsDTO">CaseDetailsDTO</h2>
<!-- backwards compatibility -->
<a id="schemacasedetailsdto"></a>
<a id="schema_CaseDetailsDTO"></a>
<a id="tocScasedetailsdto"></a>
<a id="tocscasedetailsdto"></a>

```json
{
  "id": "a11e3456-e89b-12d3-a456-426614174000",
  "caseRef": 100000000000001,
  "uprn": "10008677190",
  "estabUprn": "10008677190",
  "caseType": "HH",
  "addressType": "HH",
  "estabType": "HOUSEHOLD",
  "addressLevel": "U",
  "abpCode": "RD06",
  "organisationName": "Acme Corporation",
  "addressLine1": "Flat 51 Francombe House",
  "addressLine2": "Commercial Road",
  "addressLine3": "Suite 3",
  "townName": "Windleybury",
  "postcode": "XX1 0XX",
  "latitude": "51.5074",
  "longitude": "-0.1278",
  "oa": "E00073438",
  "lsoa": "E01014540",
  "msoa": "E02003043",
  "lad": "E06000023",
  "region": "E12000009",
  "htcWillingness": "3",
  "htcDigital": "4",
  "fieldCoordinatorId": "FC12344",
  "fieldOfficerId": "FO12345",
  "treatmentCode": "HH_PSCE",
  "ceExpectedCapacity": 1505,
  "ceActualResponses": 1504,
  "collectionExerciseId": "b22e3456-e89b-12d3-a456-426614174000",
  "createdDateTime": "2024-01-15T10:30:00Z",
  "events": [],
  "receiptReceived": true,
  "refusalReceived": "SOFT_REFUSAL",
  "invalid": false,
  "lastUpdated": "2024-01-20T14:45:00Z",
  "printBatch": "15",
  "surveyLaunched": true
}

```

Comprehensive Data Transfer Object containing detailed case attributes

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|abpCode|string|false|none|AddressBase classification code|
|addressLevel|string|false|none|Address level classification (e.g., E, U (signifying Establishment and Unit))|
|addressLine1|string|false|none|First address line|
|addressLine2|string|false|none|Second address line|
|addressLine3|string|false|none|Third address line|
|addressType|string|false|none|Residential address frame type (e.g., HH, CE)|
|caseRef|integer(int64)|true|none|Unique numeric reference for the case|
|caseType|string|false|none|Case type classification (e.g., HH, HI, CE). It will match addressType unless it is an individual (HI) case.|
|ceActualResponses|integer(int32)|false|none|Actual number of responses received for Communal Establishment|
|ceExpectedCapacity|integer(int32)|false|none|Expected resident capacity (bedspaces) of communal establishments (CE)|
|collectionExerciseId|string(uuid)|false|none|Collection Exercise UUID identifier|
|createdDateTime|string(date-time)|false|none|Date and time when the case was created|
|estabType|string|false|none|Establishment type (e.g., HALL OF RESIDENCE, HOUSEHOLD, SHELTERED ACCOMMODATION, RESIDENTIAL CARAVAN, RESIDENTIAL BOAT)|
|estabUprn|string|false|none|Establishment UPRN for non-household establishments|
|events|[[CaseDetailsEventDTO](#schemacasedetailseventdto)]|false|none|List of events associated with this case|
|fieldCoordinatorId|string|false|none|Field Coordinator identifier for the assigned case|
|fieldOfficerId|string|false|none|Field Officer identifier for the assigned case|
|htcDigital|string|false|none|Hard to Count Index - Digital (1-5) indicator|
|htcWillingness|string|false|none|Hard to Count Index - Willingness (1-5) indicator|
|id|string(uuid)|true|none|Unique case UUID identifier|
|invalid|boolean|false|none|Flag indicating if the case record is marked as invalid|
|lad|string|false|none|Local Authority District code (e.g., N06000023, S06000023, E06000023, W06000023)|
|lastUpdated|string(date-time)|false|none|Date and time when the case was last updated|
|latitude|string|false|none|Geographic latitude coordinate|
|longitude|string|false|none|Geographic longitude coordinate|
|lsoa|string|false|none|Lower Layer Super Output Area grid reference (e.g., N01014540, S01014540, E01014540, W01014540)|
|msoa|string|false|none|Middle Layer Super Output Area grid reference (e.g., N02003043, S02003043, E02003043, W02003043)|
|oa|string|false|none|Output Area grid reference (e.g., N00073438, S00073438, E00073438, W00073438)|
|organisationName|string|false|none|Name of the organisation at the address|
|postcode|string|false|none|UK postal code|
|printBatch|string|false|none|Print batch identifier for household initial contact material|
|receiptReceived|boolean|false|none|Flag indicating if receipt has been received from respondent|
|refusalReceived|string|false|none|Type of refusal received (HARD_REFUSAL, SOFT_REFUSAL, or null)|
|region|string|false|none|Administrative region code (e.g., N12000009, S12000009, E12000009, W12000009)|
|surveyLaunched|boolean|false|none|Flag indicating if survey has been launched to respondent|
|townName|string|false|none|Town or city name|
|treatmentCode|string|false|none|Treatment code (one of the appropriate ones for the region (e.g., HH_PSCE, HH_PSLE, HH_PNCE, HH_PNLE, HH_OSCE, HH_OSLE, HH_ONCE, HH_ONLE, HH_PSCW, HH_PSLW, HH_PNCW, HH_PN, HH_OSCW, HH_OSLW, HH_ONCW, HH_ONLW, HH_OGXS, HH_OSXS, HH_PBXN, HH_OAXN, HH_OBXN)) indicating special handling or processing instructions for the case|
|uprn|string|false|none|Unique Property Reference Number|

#### Enumerated Values

|Property|Value|
|---|---|
|refusalReceived|HARD_REFUSAL|
|refusalReceived|EXTRAORDINARY_REFUSAL|

<h2 id="tocS_CaseDetailsEventDTO">CaseDetailsEventDTO</h2>
<!-- backwards compatibility -->
<a id="schemacasedetailseventdto"></a>
<a id="schema_CaseDetailsEventDTO"></a>
<a id="tocScasedetailseventdto"></a>
<a id="tocscasedetailseventdto"></a>

```json
{
  "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "eventType": "NEW_CASE",
  "eventDescription": "Case created for collection exercise",
  "eventDate": "2024-01-15T10:30:00Z",
  "eventChannel": "RM",
  "eventTransactionId": "d290f1ee-6c54-4b01-90e6-d701748f0851",
  "rmEventProcessed": "2024-01-15T10:35:00Z",
  "eventSource": "UAC_SERVICE",
  "eventPayload": "{\"caseRef\":\"100000000000001\"}",
  "messageTimestamp": "2024-01-15T10:30:00Z"
}

```

Detailed Data Transfer Object containing event attributes for case details response

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|eventChannel|string|true|none|Channel through which the event was triggered (RM, CC, etc.)|
|eventDate|string(date-time)|true|none|Date and time when the event occurred|
|eventDescription|string|false|none|Human-readable description of the event|
|eventPayload|string|false|none|JSON payload containing event-specific data|
|eventSource|string|false|none|Source system that generated the event (e.g., UAC_SERVICE, PRINT_SERVICE)|
|eventTransactionId|string(uuid)|false|none|Unique transaction identifier for this event|
|eventType|string|true|none|Type of event (e.g., NEW_CASE, RECEIPT, REFUSAL, EQ_LAUNCH, UAC_AUTHENTICATION)|
|id|string(uuid)|true|none|Unique event identifier (UUID)|
|messageTimestamp|string(date-time)|false|none|Message timestamp from the originating system|
|rmEventProcessed|string(date-time)|false|none|Date and time when the event was processed by RM|

<h2 id="tocS_CaseEventDTO">CaseEventDTO</h2>
<!-- backwards compatibility -->
<a id="schemacaseeventdto"></a>
<a id="schema_CaseEventDTO"></a>
<a id="tocScaseeventdto"></a>
<a id="tocscaseeventdto"></a>

```json
{
  "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "eventType": "NEW_CASE",
  "description": "Case created for collection exercise",
  "createdDateTime": "2024-01-15T10:30:00Z"
}

```

Data Transfer Object representing an event associated with a case

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|createdDateTime|string(date-time)|true|none|Date and time when the event was created|
|description|string|false|none|Human-readable description of the event|
|eventType|[EventTypeDTO](#schemaeventtypedto)|true|none|Enumeration of all possible event types that can occur in the census RM system|
|id|string(uuid)|true|none|Unique event identifier (UUID)|

<h2 id="tocS_EventTypeDTO">EventTypeDTO</h2>
<!-- backwards compatibility -->
<a id="schemaeventtypedto"></a>
<a id="schema_EventTypeDTO"></a>
<a id="tocSeventtypedto"></a>
<a id="tocseventtypedto"></a>

```json
"NEW_CASE"

```

Enumeration of all possible event types that can occur in the census RM system

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|string|false|none|Enumeration of all possible event types that can occur in the census RM system|

#### Enumerated Values

|Property|Value|
|---|---|
|*anonymous*|NEW_CASE|
|*anonymous*|RECEIPT|
|*anonymous*|REFUSAL|
|*anonymous*|INVALID_CASE|
|*anonymous*|EQ_LAUNCH|
|*anonymous*|UAC_AUTHENTICATION|
|*anonymous*|PRINT_FULFILMENT|
|*anonymous*|EXPORT_FILE|
|*anonymous*|DEACTIVATE_UAC|
|*anonymous*|UPDATE_SAMPLE|
|*anonymous*|UPDATE_SAMPLE_SENSITIVE|
|*anonymous*|SMS_FULFILMENT|
|*anonymous*|ACTION_RULE_SMS_REQUEST|
|*anonymous*|EMAIL_FULFILMENT|
|*anonymous*|ACTION_RULE_EMAIL_REQUEST|
|*anonymous*|ACTION_RULE_SMS_CONFIRMATION|
|*anonymous*|ACTION_RULE_EMAIL_CONFIRMATION|
|*anonymous*|ERASE_DATA|

<h2 id="tocS_NewQidLink">NewQidLink</h2>
<!-- backwards compatibility -->
<a id="schemanewqidlink"></a>
<a id="schema_NewQidLink"></a>
<a id="tocSnewqidlink"></a>
<a id="tocsnewqidlink"></a>

```json
{
  "transactionId": "a11e3456-e89b-12d3-a456-426614174000",
  "channel": "CONTACT_CENTRE",
  "qidLink": {
    "questionnaireId": "Q123456",
    "caseId": "a11e3456-e89b-12d3-a456-426614174000"
  }
}

```

Data Transfer Object for creating a new QID link to a case

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|channel|string|false|none|Channel through which this link was created (e.g., CONTACT_CENTRE, PHONE, ONLINE)|
|qidLink|[QidLink](#schemaqidlink)|false|none|Data Transfer Object representing the link between a Questionnaire ID (QID) and a Case|
|transactionId|string(uuid)|false|none|Transaction ID for tracing this QID link request|

<h2 id="tocS_QidLink">QidLink</h2>
<!-- backwards compatibility -->
<a id="schemaqidlink"></a>
<a id="schema_QidLink"></a>
<a id="tocSqidlink"></a>
<a id="tocsqidlink"></a>

```json
{
  "questionnaireId": "Q123456",
  "caseId": "a11e3456-e89b-12d3-a456-426614174000"
}

```

Data Transfer Object representing the link between a Questionnaire ID (QID) and a Case

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|caseId|string(uuid)|false|none|UUID of the census case associated with this QID|
|questionnaireId|string|true|none|Unique Questionnaire Identifier (QID) assigned to the respondent|

