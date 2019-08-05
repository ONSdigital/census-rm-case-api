package uk.gov.ons.census.caseapisvc.endpoint;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UacQidEndpointIT {

  @LocalServerPort private int port;

  @Test
  public void shouldRetrieveUacQidPair() throws Exception {
    String url = String.format("http://localhost:%d/uacqid/create", port);
    Map<String, String> headers = new HashMap<>();
    headers.put("accept", "application/json");
    headers.put("Content-Type", "application/json");
    HttpResponse<JsonNode> response =
        Unirest.post(url)
            .headers(headers)
            .body("{\"questionnaire_id\":\"1\"}")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(CREATED.value());
  }

}
