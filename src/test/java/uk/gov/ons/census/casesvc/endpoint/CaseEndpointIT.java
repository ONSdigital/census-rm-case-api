package uk.gov.ons.census.casesvc.endpoint;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.census.casesvc.model.entity.Case;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CaseEndpointIT {

  @LocalServerPort private int port;

  TestRestTemplate restTemplate = new TestRestTemplate();

  HttpHeaders headers = new HttpHeaders();

  @Test
  public void shouldReturnCaseWhenCaseIdExists() throws Exception {

    assert(false);
    //TODO something along these line but needs security disabling as giving 401
//    HttpEntity<String> entity = new HttpEntity<>(null, headers);
//
//    ResponseEntity<Case> response =
//        restTemplate.exchange(
//            createURLWithPort("/cases/77216a62-c879-4fd8-b7df-6d7d0421d2d7"),
//            HttpMethod.GET,
//            entity,
//            Case.class);
//
//    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
// blah, blah, blah
  }

  private String createURLWithPort(String uri) {
    return "http://localhost:" + port + uri;
  }
}
