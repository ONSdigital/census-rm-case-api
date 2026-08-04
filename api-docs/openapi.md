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

Service for managing case data

Base URLs:

* <a href="http://localhost:8161">http://localhost:8161</a>

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
  "abpCode": "string",
  "addressLevel": "string",
  "addressLine1": "string",
  "addressLine2": "string",
  "addressLine3": "string",
  "addressType": "string",
  "caseRef": 0,
  "caseType": "string",
  "ceActualResponses": 0,
  "ceExpectedCapacity": 0,
  "collectionExerciseId": "1156afc3-b136-4e6d-860e-e98a7c9c78f5",
  "createdDateTime": "2019-08-24T14:15:22Z",
  "estabType": "string",
  "estabUprn": "string",
  "events": [
    {
      "eventChannel": "string",
      "eventDate": "2019-08-24T14:15:22Z",
      "eventDescription": "string",
      "eventPayload": "string",
      "eventSource": "string",
      "eventTransactionId": "2e360fa7-9633-4a7e-93c7-4bccb9c79e22",
      "eventType": "string",
      "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
      "messageTimestamp": "2019-08-24T14:15:22Z",
      "rmEventProcessed": "2019-08-24T14:15:22Z"
    }
  ],
  "fieldCoordinatorId": "string",
  "fieldOfficerId": "string",
  "htcDigital": "string",
  "htcWillingness": "string",
  "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
  "invalid": true,
  "lad": "string",
  "lastUpdated": "2019-08-24T14:15:22Z",
  "latitude": "string",
  "longitude": "string",
  "lsoa": "string",
  "msoa": "string",
  "oa": "string",
  "organisationName": "string",
  "postcode": "string",
  "printBatch": "string",
  "receiptReceived": true,
  "refusalReceived": "HARD_REFUSAL",
  "region": "string",
  "surveyLaunched": true,
  "townName": "string",
  "treatmentCode": "string",
  "uprn": "string"
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
    "abpCode": "string",
    "addressLevel": "string",
    "addressLine1": "string",
    "addressLine2": "string",
    "addressLine3": "string",
    "addressType": "string",
    "caseEvents": [
      {
        "createdDateTime": "2019-08-24T14:15:22Z",
        "description": "string",
        "eventType": "NEW_CASE",
        "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08"
      }
    ],
    "caseRef": "100000000000001",
    "caseType": "string",
    "collectionExerciseId": "1156afc3-b136-4e6d-860e-e98a7c9c78f5",
    "createdDateTime": "2019-08-24T14:15:22Z",
    "estabType": "string",
    "estabUprn": "string",
    "id": "a11e3456-e89b-12d3-a456-426614174000",
    "invalid": true,
    "lad": "string",
    "lastUpdated": "2019-08-24T14:15:22Z",
    "latitude": "string",
    "longitude": "string",
    "lsoa": "string",
    "msoa": "string",
    "oa": "string",
    "organisationName": "string",
    "postcode": "string",
    "region": "string",
    "secureEstablishment": true,
    "surveyType": "string",
    "townName": "string",
    "uprn": "string"
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
|» abpCode|string|false|none|none|
|» addressLevel|string|false|none|none|
|» addressLine1|string|false|none|none|
|» addressLine2|string|false|none|none|
|» addressLine3|string|false|none|none|
|» addressType|string|false|none|none|
|» caseEvents|[[CaseEventDTO](#schemacaseeventdto)]|false|none|none|
|»» createdDateTime|string(date-time)|false|none|none|
|»» description|string|false|none|none|
|»» eventType|string|false|none|none|
|»» id|string(uuid)|false|none|none|
|» caseRef|string|true|none|Unique numeric reference for the case|
|» caseType|string|false|none|none|
|» collectionExerciseId|string(uuid)|false|none|none|
|» createdDateTime|string(date-time)|false|none|none|
|» estabType|string|false|none|none|
|» estabUprn|string|false|none|none|
|» id|string(uuid)|true|none|Unique case UUID identifier|
|» invalid|boolean|false|none|none|
|» lad|string|false|none|none|
|» lastUpdated|string(date-time)|false|none|none|
|» latitude|string|false|none|none|
|» longitude|string|false|none|none|
|» lsoa|string|false|none|none|
|» msoa|string|false|none|none|
|» oa|string|false|none|none|
|» organisationName|string|false|none|none|
|» postcode|string|false|none|none|
|» region|string|false|none|none|
|» secureEstablishment|boolean|false|none|none|
|» surveyType|string|false|none|none|
|» townName|string|false|none|none|
|» uprn|string|false|none|none|

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
  "abpCode": "string",
  "addressLevel": "string",
  "addressLine1": "string",
  "addressLine2": "string",
  "addressLine3": "string",
  "addressType": "string",
  "caseEvents": [
    {
      "createdDateTime": "2019-08-24T14:15:22Z",
      "description": "string",
      "eventType": "NEW_CASE",
      "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08"
    }
  ],
  "caseRef": "100000000000001",
  "caseType": "string",
  "collectionExerciseId": "1156afc3-b136-4e6d-860e-e98a7c9c78f5",
  "createdDateTime": "2019-08-24T14:15:22Z",
  "estabType": "string",
  "estabUprn": "string",
  "id": "a11e3456-e89b-12d3-a456-426614174000",
  "invalid": true,
  "lad": "string",
  "lastUpdated": "2019-08-24T14:15:22Z",
  "latitude": "string",
  "longitude": "string",
  "lsoa": "string",
  "msoa": "string",
  "oa": "string",
  "organisationName": "string",
  "postcode": "string",
  "region": "string",
  "secureEstablishment": true,
  "surveyType": "string",
  "townName": "string",
  "uprn": "string"
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
  "abpCode": "string",
  "addressLevel": "string",
  "addressLine1": "string",
  "addressLine2": "string",
  "addressLine3": "string",
  "addressType": "string",
  "caseEvents": [
    {
      "createdDateTime": "2019-08-24T14:15:22Z",
      "description": "string",
      "eventType": "NEW_CASE",
      "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08"
    }
  ],
  "caseRef": "100000000000001",
  "caseType": "string",
  "collectionExerciseId": "1156afc3-b136-4e6d-860e-e98a7c9c78f5",
  "createdDateTime": "2019-08-24T14:15:22Z",
  "estabType": "string",
  "estabUprn": "string",
  "id": "a11e3456-e89b-12d3-a456-426614174000",
  "invalid": true,
  "lad": "string",
  "lastUpdated": "2019-08-24T14:15:22Z",
  "latitude": "string",
  "longitude": "string",
  "lsoa": "string",
  "msoa": "string",
  "oa": "string",
  "organisationName": "string",
  "postcode": "string",
  "region": "string",
  "secureEstablishment": true,
  "surveyType": "string",
  "townName": "string",
  "uprn": "string"
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
    "abpCode": "string",
    "addressLevel": "string",
    "addressLine1": "string",
    "addressLine2": "string",
    "addressLine3": "string",
    "addressType": "string",
    "caseEvents": [
      {
        "createdDateTime": "2019-08-24T14:15:22Z",
        "description": "string",
        "eventType": "NEW_CASE",
        "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08"
      }
    ],
    "caseRef": "100000000000001",
    "caseType": "string",
    "collectionExerciseId": "1156afc3-b136-4e6d-860e-e98a7c9c78f5",
    "createdDateTime": "2019-08-24T14:15:22Z",
    "estabType": "string",
    "estabUprn": "string",
    "id": "a11e3456-e89b-12d3-a456-426614174000",
    "invalid": true,
    "lad": "string",
    "lastUpdated": "2019-08-24T14:15:22Z",
    "latitude": "string",
    "longitude": "string",
    "lsoa": "string",
    "msoa": "string",
    "oa": "string",
    "organisationName": "string",
    "postcode": "string",
    "region": "string",
    "secureEstablishment": true,
    "surveyType": "string",
    "townName": "string",
    "uprn": "string"
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
|» abpCode|string|false|none|none|
|» addressLevel|string|false|none|none|
|» addressLine1|string|false|none|none|
|» addressLine2|string|false|none|none|
|» addressLine3|string|false|none|none|
|» addressType|string|false|none|none|
|» caseEvents|[[CaseEventDTO](#schemacaseeventdto)]|false|none|none|
|»» createdDateTime|string(date-time)|false|none|none|
|»» description|string|false|none|none|
|»» eventType|string|false|none|none|
|»» id|string(uuid)|false|none|none|
|» caseRef|string|true|none|Unique numeric reference for the case|
|» caseType|string|false|none|none|
|» collectionExerciseId|string(uuid)|false|none|none|
|» createdDateTime|string(date-time)|false|none|none|
|» estabType|string|false|none|none|
|» estabUprn|string|false|none|none|
|» id|string(uuid)|true|none|Unique case UUID identifier|
|» invalid|boolean|false|none|none|
|» lad|string|false|none|none|
|» lastUpdated|string(date-time)|false|none|none|
|» latitude|string|false|none|none|
|» longitude|string|false|none|none|
|» lsoa|string|false|none|none|
|» msoa|string|false|none|none|
|» oa|string|false|none|none|
|» organisationName|string|false|none|none|
|» postcode|string|false|none|none|
|» region|string|false|none|none|
|» secureEstablishment|boolean|false|none|none|
|» surveyType|string|false|none|none|
|» townName|string|false|none|none|
|» uprn|string|false|none|none|

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
  "abpCode": "string",
  "addressLevel": "string",
  "addressLine1": "string",
  "addressLine2": "string",
  "addressLine3": "string",
  "addressType": "string",
  "caseEvents": [
    {
      "createdDateTime": "2019-08-24T14:15:22Z",
      "description": "string",
      "eventType": "NEW_CASE",
      "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08"
    }
  ],
  "caseRef": "100000000000001",
  "caseType": "string",
  "collectionExerciseId": "1156afc3-b136-4e6d-860e-e98a7c9c78f5",
  "createdDateTime": "2019-08-24T14:15:22Z",
  "estabType": "string",
  "estabUprn": "string",
  "id": "a11e3456-e89b-12d3-a456-426614174000",
  "invalid": true,
  "lad": "string",
  "lastUpdated": "2019-08-24T14:15:22Z",
  "latitude": "string",
  "longitude": "string",
  "lsoa": "string",
  "msoa": "string",
  "oa": "string",
  "organisationName": "string",
  "postcode": "string",
  "region": "string",
  "secureEstablishment": true,
  "surveyType": "string",
  "townName": "string",
  "uprn": "string"
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

<h1 id="case-api-service-qid-endpoint">qid-endpoint</h1>

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
  "channel": "string",
  "qidLink": {
    "caseId": "af51d69f-996a-4891-a745-aadfcdec225a",
    "questionnaireId": "string"
  },
  "transactionId": "75906707-8c31-479c-b354-aa805c4cefbc"
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

> Body parameter

```json
{
  "channel": "string",
  "qidLink": {
    "caseId": "af51d69f-996a-4891-a745-aadfcdec225a",
    "questionnaireId": "string"
  },
  "transactionId": "75906707-8c31-479c-b354-aa805c4cefbc"
}
```

<h3 id="putqidlinktocase-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[NewQidLink](#schemanewqidlink)|true|none|

<h3 id="putqidlinktocase-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|None|

<aside class="success">
This operation does not require authentication
</aside>

## getUacQidLinkByQid

<a id="opIdgetUacQidLinkByQid"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8161/qids/{qid} \
  -H 'Accept: */*'

```

```http
GET http://localhost:8161/qids/{qid} HTTP/1.1
Host: localhost:8161
Accept: */*

```

```javascript

const headers = {
  'Accept':'*/*'
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
  'Accept' => '*/*'
}

result = RestClient.get 'http://localhost:8161/qids/{qid}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': '*/*'
}

r = requests.get('http://localhost:8161/qids/{qid}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => '*/*',
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
        "Accept": []string{"*/*"},
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

<h3 id="getuacqidlinkbyqid-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|qid|path|string|true|none|

> Example responses

> 200 Response

<h3 id="getuacqidlinkbyqid-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[QidLink](#schemaqidlink)|

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
  "abpCode": "string",
  "addressLevel": "string",
  "addressLine1": "string",
  "addressLine2": "string",
  "addressLine3": "string",
  "addressType": "string",
  "caseEvents": [
    {
      "createdDateTime": "2019-08-24T14:15:22Z",
      "description": "string",
      "eventType": "NEW_CASE",
      "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08"
    }
  ],
  "caseRef": "100000000000001",
  "caseType": "string",
  "collectionExerciseId": "1156afc3-b136-4e6d-860e-e98a7c9c78f5",
  "createdDateTime": "2019-08-24T14:15:22Z",
  "estabType": "string",
  "estabUprn": "string",
  "id": "a11e3456-e89b-12d3-a456-426614174000",
  "invalid": true,
  "lad": "string",
  "lastUpdated": "2019-08-24T14:15:22Z",
  "latitude": "string",
  "longitude": "string",
  "lsoa": "string",
  "msoa": "string",
  "oa": "string",
  "organisationName": "string",
  "postcode": "string",
  "region": "string",
  "secureEstablishment": true,
  "surveyType": "string",
  "townName": "string",
  "uprn": "string"
}

```

Data Transfer Object representing a census case container

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|abpCode|string|false|none|none|
|addressLevel|string|false|none|none|
|addressLine1|string|false|none|none|
|addressLine2|string|false|none|none|
|addressLine3|string|false|none|none|
|addressType|string|false|none|none|
|caseEvents|[[CaseEventDTO](#schemacaseeventdto)]|false|none|none|
|caseRef|string|true|none|Unique numeric reference for the case|
|caseType|string|false|none|none|
|collectionExerciseId|string(uuid)|false|none|none|
|createdDateTime|string(date-time)|false|none|none|
|estabType|string|false|none|none|
|estabUprn|string|false|none|none|
|id|string(uuid)|true|none|Unique case UUID identifier|
|invalid|boolean|false|none|none|
|lad|string|false|none|none|
|lastUpdated|string(date-time)|false|none|none|
|latitude|string|false|none|none|
|longitude|string|false|none|none|
|lsoa|string|false|none|none|
|msoa|string|false|none|none|
|oa|string|false|none|none|
|organisationName|string|false|none|none|
|postcode|string|false|none|none|
|region|string|false|none|none|
|secureEstablishment|boolean|false|none|none|
|surveyType|string|false|none|none|
|townName|string|false|none|none|
|uprn|string|false|none|none|

<h2 id="tocS_CaseDetailsDTO">CaseDetailsDTO</h2>
<!-- backwards compatibility -->
<a id="schemacasedetailsdto"></a>
<a id="schema_CaseDetailsDTO"></a>
<a id="tocScasedetailsdto"></a>
<a id="tocscasedetailsdto"></a>

```json
{
  "abpCode": "string",
  "addressLevel": "string",
  "addressLine1": "string",
  "addressLine2": "string",
  "addressLine3": "string",
  "addressType": "string",
  "caseRef": 0,
  "caseType": "string",
  "ceActualResponses": 0,
  "ceExpectedCapacity": 0,
  "collectionExerciseId": "1156afc3-b136-4e6d-860e-e98a7c9c78f5",
  "createdDateTime": "2019-08-24T14:15:22Z",
  "estabType": "string",
  "estabUprn": "string",
  "events": [
    {
      "eventChannel": "string",
      "eventDate": "2019-08-24T14:15:22Z",
      "eventDescription": "string",
      "eventPayload": "string",
      "eventSource": "string",
      "eventTransactionId": "2e360fa7-9633-4a7e-93c7-4bccb9c79e22",
      "eventType": "string",
      "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
      "messageTimestamp": "2019-08-24T14:15:22Z",
      "rmEventProcessed": "2019-08-24T14:15:22Z"
    }
  ],
  "fieldCoordinatorId": "string",
  "fieldOfficerId": "string",
  "htcDigital": "string",
  "htcWillingness": "string",
  "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
  "invalid": true,
  "lad": "string",
  "lastUpdated": "2019-08-24T14:15:22Z",
  "latitude": "string",
  "longitude": "string",
  "lsoa": "string",
  "msoa": "string",
  "oa": "string",
  "organisationName": "string",
  "postcode": "string",
  "printBatch": "string",
  "receiptReceived": true,
  "refusalReceived": "HARD_REFUSAL",
  "region": "string",
  "surveyLaunched": true,
  "townName": "string",
  "treatmentCode": "string",
  "uprn": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|abpCode|string|false|none|none|
|addressLevel|string|false|none|none|
|addressLine1|string|false|none|none|
|addressLine2|string|false|none|none|
|addressLine3|string|false|none|none|
|addressType|string|false|none|none|
|caseRef|integer(int64)|false|none|none|
|caseType|string|false|none|none|
|ceActualResponses|integer(int32)|false|none|none|
|ceExpectedCapacity|integer(int32)|false|none|none|
|collectionExerciseId|string(uuid)|false|none|none|
|createdDateTime|string(date-time)|false|none|none|
|estabType|string|false|none|none|
|estabUprn|string|false|none|none|
|events|[[CaseDetailsEventDTO](#schemacasedetailseventdto)]|false|none|none|
|fieldCoordinatorId|string|false|none|none|
|fieldOfficerId|string|false|none|none|
|htcDigital|string|false|none|none|
|htcWillingness|string|false|none|none|
|id|string(uuid)|false|none|none|
|invalid|boolean|false|none|none|
|lad|string|false|none|none|
|lastUpdated|string(date-time)|false|none|none|
|latitude|string|false|none|none|
|longitude|string|false|none|none|
|lsoa|string|false|none|none|
|msoa|string|false|none|none|
|oa|string|false|none|none|
|organisationName|string|false|none|none|
|postcode|string|false|none|none|
|printBatch|string|false|none|none|
|receiptReceived|boolean|false|none|none|
|refusalReceived|string|false|none|none|
|region|string|false|none|none|
|surveyLaunched|boolean|false|none|none|
|townName|string|false|none|none|
|treatmentCode|string|false|none|none|
|uprn|string|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|refusalReceived|HARD_REFUSAL|
|refusalReceived|EXTRAORDINARY_REFUSAL|
|refusalReceived|SOFT_REFUSAL|
|refusalReceived|WITHDRAWAL_REFUSAL|

<h2 id="tocS_CaseDetailsEventDTO">CaseDetailsEventDTO</h2>
<!-- backwards compatibility -->
<a id="schemacasedetailseventdto"></a>
<a id="schema_CaseDetailsEventDTO"></a>
<a id="tocScasedetailseventdto"></a>
<a id="tocscasedetailseventdto"></a>

```json
{
  "eventChannel": "string",
  "eventDate": "2019-08-24T14:15:22Z",
  "eventDescription": "string",
  "eventPayload": "string",
  "eventSource": "string",
  "eventTransactionId": "2e360fa7-9633-4a7e-93c7-4bccb9c79e22",
  "eventType": "string",
  "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
  "messageTimestamp": "2019-08-24T14:15:22Z",
  "rmEventProcessed": "2019-08-24T14:15:22Z"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|eventChannel|string|false|none|none|
|eventDate|string(date-time)|false|none|none|
|eventDescription|string|false|none|none|
|eventPayload|string|false|none|none|
|eventSource|string|false|none|none|
|eventTransactionId|string(uuid)|false|none|none|
|eventType|string|false|none|none|
|id|string(uuid)|false|none|none|
|messageTimestamp|string(date-time)|false|none|none|
|rmEventProcessed|string(date-time)|false|none|none|

<h2 id="tocS_CaseEventDTO">CaseEventDTO</h2>
<!-- backwards compatibility -->
<a id="schemacaseeventdto"></a>
<a id="schema_CaseEventDTO"></a>
<a id="tocScaseeventdto"></a>
<a id="tocscaseeventdto"></a>

```json
{
  "createdDateTime": "2019-08-24T14:15:22Z",
  "description": "string",
  "eventType": "NEW_CASE",
  "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|createdDateTime|string(date-time)|false|none|none|
|description|string|false|none|none|
|eventType|string|false|none|none|
|id|string(uuid)|false|none|none|

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

<h2 id="tocS_NewQidLink">NewQidLink</h2>
<!-- backwards compatibility -->
<a id="schemanewqidlink"></a>
<a id="schema_NewQidLink"></a>
<a id="tocSnewqidlink"></a>
<a id="tocsnewqidlink"></a>

```json
{
  "channel": "string",
  "qidLink": {
    "caseId": "af51d69f-996a-4891-a745-aadfcdec225a",
    "questionnaireId": "string"
  },
  "transactionId": "75906707-8c31-479c-b354-aa805c4cefbc"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|channel|string|false|none|none|
|qidLink|[QidLink](#schemaqidlink)|false|none|none|
|transactionId|string(uuid)|false|none|none|

<h2 id="tocS_QidLink">QidLink</h2>
<!-- backwards compatibility -->
<a id="schemaqidlink"></a>
<a id="schema_QidLink"></a>
<a id="tocSqidlink"></a>
<a id="tocsqidlink"></a>

```json
{
  "caseId": "af51d69f-996a-4891-a745-aadfcdec225a",
  "questionnaireId": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|caseId|string(uuid)|false|none|none|
|questionnaireId|string|false|none|none|

