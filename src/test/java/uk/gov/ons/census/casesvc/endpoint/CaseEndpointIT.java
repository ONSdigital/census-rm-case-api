package uk.gov.ons.census.casesvc.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.common.UnirestInitialiser;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CaseEndpointIT {

  private String TEST1_CASE_ID = "7f043f01-2f84-48ee-a4a0-850735b09692";
  private String TEST1_CASE_REFERENCE_ID = "10000002";

  //  private UUID TEST2_CASE_ID = UUID.fromString("3e948f6a-00bb-466d-88a7-b0990a827b53");
  //
  //  private UUID TEST3_CASE_ID = UUID.fromString("4ee74fac-e0fd-41ac-8322-414b8d5e978d");
  //
  //  private String TEST_UPRN = "123456789012345";

  @LocalServerPort private int port;

  @BeforeClass
  public static void setUp() throws InterruptedException {
    ObjectMapper value = new ObjectMapper();
    UnirestInitialiser.initialise(value);
    Thread.sleep(2000);
  }

  @Test
  public void shouldReturnACaseWithEventsWhenSearchingByCaseId() throws Exception {

    String url = String.format("http://localhost:%d/cases/%s", port, TEST1_CASE_ID);

    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(url)
            .header("accept", "application/json")
            .queryString("caseEvents", "true")
            .asJson();

    assertThat(jsonResponse.getBody()).isNotNull();
    assertThat(jsonResponse.getStatus()).isEqualTo(OK.value());

    JSONObject jsonCase = jsonResponse.getBody().getObject();
    assertThat(jsonCase.get("caseRef")).isEqualTo(TEST1_CASE_REFERENCE_ID);

    JSONArray jsonCaseEvents = (JSONArray) jsonCase.get("caseEvents");
    assertThat(jsonCaseEvents.length()).isGreaterThan(0);
  }

  private String createURLWithPort(String uri) {
    return "http://localhost:" + port + uri;
  }
}
