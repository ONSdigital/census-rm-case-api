package uk.gov.ons.census.caseapisvc.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.census.caseapisvc.model.dto.ResponseManagementEvent;
import uk.gov.ons.census.caseapisvc.utility.RabbitQueueHelper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UacQidEndpointIT {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @LocalServerPort private int port;
  private String createUacUrl = null;

  @Autowired private RabbitQueueHelper rabbitQueueHelper;

  @Value("${queueconfig.uac-qid-created-queue}")
  private String uacQidCreatedQueueName;

  @Before
  @Transactional
  public void setUp() {
    rabbitQueueHelper.purgeQueue(uacQidCreatedQueueName);
    this.createUacUrl = String.format("http://localhost:%d/uacqid/create", port);
  }

  @Test
  public void shouldRetrieveUacQidPair() throws Exception {
    // Given
    UUID caseId = UUID.randomUUID();

    // When
    HttpResponse<JsonNode> response = sendUacQidCreateRequest("1", caseId);

    // Then
    assertThat(response.getStatus()).isEqualTo(CREATED.value());
    assertThat(response.getBody().getObject().get("uac")).isNotNull();
    assertThat(response.getBody().getObject().get("qid").toString()).startsWith("01");
    assertThat(response.getBody().getObject().get("caseId")).isEqualTo(caseId.toString());
  }

  @Test
  @DirtiesContext
  public void shouldDistributeUacQidCreatedEvent()
      throws UnirestException, IOException, InterruptedException {
    // Given
    UUID caseId = UUID.randomUUID();
    BlockingQueue<String> uacQidCreatedQueue = rabbitQueueHelper.listen(uacQidCreatedQueueName);

    // When
    HttpResponse<JsonNode> response = sendUacQidCreateRequest("32", caseId);

    // Then
    String message = rabbitQueueHelper.checkExpectedMessageReceived(uacQidCreatedQueue);
    ResponseManagementEvent responseManagementEvent = objectMapper.readValue(message, ResponseManagementEvent.class);

    assertThat(responseManagementEvent.getPayload().getUacQidCreated().getCaseId())
        .isEqualTo(caseId.toString());
    assertThat(responseManagementEvent.getPayload().getUacQidCreated().getQid()).startsWith("32");
    assertThat(responseManagementEvent.getPayload().getUacQidCreated().getUac()).isNotNull();
    assertThat(responseManagementEvent.getEvent().getSource()).isEqualTo("RESPONSE_MANAGEMENT");
    assertThat(responseManagementEvent.getEvent().getChannel()).isEqualTo("RM");
    assertThat(responseManagementEvent.getEvent().getType()).isEqualTo("RM_UAC_CREATED");
  }

  private String buildCaseDetailsJSON(String questionnaireType, UUID caseId) {
    return String.format(
        "{\"questionnaireType\":\"%s\", \"caseId\":\"%s\"}", questionnaireType, caseId.toString());
  }

  private HttpResponse<JsonNode> sendUacQidCreateRequest(String questionnaireType, UUID caseId)
      throws UnirestException {
    Map<String, String> headers = new HashMap<>();
    headers.put("accept", "application/json");
    headers.put("Content-Type", "application/json");
    return Unirest.post(this.createUacUrl)
        .headers(headers)
        .body(buildCaseDetailsJSON(questionnaireType, caseId))
        .asJson();
  }
}
