package uk.gov.ons.census.caseapisvc.endpoint;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import org.apache.http.HttpStatus;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.census.caseapisvc.model.dto.NewQidLink;
import uk.gov.ons.census.caseapisvc.model.dto.QidLink;
import uk.gov.ons.census.caseapisvc.model.dto.ResponseManagementEvent;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.model.entity.UacQidLink;
import uk.gov.ons.census.caseapisvc.model.repository.CaseRepository;
import uk.gov.ons.census.caseapisvc.model.repository.EventRepository;
import uk.gov.ons.census.caseapisvc.model.repository.UacQidLinkRepository;
import uk.gov.ons.census.caseapisvc.utility.DataUtils;
import uk.gov.ons.census.caseapisvc.utility.RabbitQueueHelper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class QidEndpointIT {
  private static final String VALID_QID = "valid_qid";

  @LocalServerPort private int port;

  @Autowired private CaseRepository caseRepo;
  @Autowired private UacQidLinkRepository uacQidLinkRepository;
  @Autowired private EventRepository eventRepository;
  @Autowired private RabbitQueueHelper rabbitQueueHelper;

  private EasyRandom easyRandom;

  private static final String questionnaireLinkEventQueueName = "dummy.questionnaire.updates";

  @Before
  @Transactional
  public void setUp() {
    try {
      clearDown();
    } catch (Exception e) {
      // this is expected behaviour, where the event rows are deleted, then the case-processor image
      // puts a new
      // event row on and the case table clear down fails.  2nd run should clear it down
      clearDown();
    }

    rabbitQueueHelper.purgeQueue(questionnaireLinkEventQueueName);

    easyRandom = new EasyRandom(new EasyRandomParameters().randomizationDepth(1));
  }

  public void clearDown() {
    eventRepository.deleteAllInBatch();
    uacQidLinkRepository.deleteAllInBatch();
    caseRepo.deleteAllInBatch();
  }

  @Test
  public void testGetQidLink() throws UnirestException, JsonProcessingException {
    // Given
    Case linkedCase = easyRandom.nextObject(Case.class);
    linkedCase = saveAndRetrieveCase(linkedCase);
    UacQidLink uacQidLink = setupLinkedUacQid(VALID_QID, linkedCase);

    // When
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(String.format("http://localhost:%d/qids/%s", port, VALID_QID))
            .header("accept", "application/json")
            .asJson();

    // Then
    assertThat(jsonResponse.getStatus()).isEqualTo(HttpStatus.SC_OK);
    QidLink responseQidLink =
        DataUtils.mapper.readValue(jsonResponse.getBody().getObject().toString(), QidLink.class);

    assertThat(responseQidLink.getCaseId()).isEqualTo(linkedCase.getCaseId());
    assertThat(responseQidLink.getQuestionnaireId()).isEqualTo(uacQidLink.getQid());
  }

  @Test
  public void testPutQidLinkToCase() throws UnirestException, IOException, InterruptedException {
    // Given
    Case caseToLink = easyRandom.nextObject(Case.class);
    caseToLink = saveAndRetrieveCase(caseToLink);
    UacQidLink uacQidLink = setupUnlinkedUacQid(VALID_QID);

    QidLink requestQidLink = new QidLink();
    requestQidLink.setCaseId(caseToLink.getCaseId());
    requestQidLink.setQuestionnaireId(VALID_QID);

    NewQidLink newQidLink = new NewQidLink();
    newQidLink.setQidLink(requestQidLink);
    newQidLink.setTransactionId(UUID.randomUUID());

    BlockingQueue<String> questionnaireLinkQueue =
        rabbitQueueHelper.listen(questionnaireLinkEventQueueName);

    // When
    HttpResponse<JsonNode> jsonResponse =
        Unirest.put(String.format("http://localhost:%d/qids/link", port))
            .header("content-type", "application/json")
            .body(DataUtils.mapper.writeValueAsString(newQidLink))
            .asJson();

    // Then
    assertThat(jsonResponse.getStatus()).isEqualTo(HttpStatus.SC_OK);

    // Check the proper QUESTIONNAIRE_LINKED event is sent
    String message = rabbitQueueHelper.checkExpectedMessageReceived(questionnaireLinkQueue);
    ResponseManagementEvent responseManagementEvent =
        DataUtils.mapper.readValue(message, ResponseManagementEvent.class);

    assertThat(responseManagementEvent.getEvent().getType()).isEqualTo("QUESTIONNAIRE_LINKED");
    assertThat(responseManagementEvent.getPayload().getUac().getCaseId())
        .isEqualTo(requestQidLink.getCaseId());
    assertThat(responseManagementEvent.getPayload().getUac().getQuestionnaireId())
        .isEqualTo(requestQidLink.getQuestionnaireId());
  }

  private Case saveAndRetrieveCase(Case caze) {
    caseRepo.saveAndFlush(caze);

    return caseRepo
        .findByCaseId(caze.getCaseId())
        .orElseThrow(() -> new RuntimeException("Case not found!"));
  }

  private UacQidLink setupLinkedUacQid(String qid, Case caze) {
    UacQidLink uacQidLink = new UacQidLink();
    uacQidLink.setId(UUID.randomUUID());
    uacQidLink.setCaze(caze);
    uacQidLink.setQid(qid);

    return saveAndRetrieveUacQidLink(uacQidLink);
  }

  private UacQidLink setupUnlinkedUacQid(String qid) {
    UacQidLink uacQidLink = new UacQidLink();
    uacQidLink.setId(UUID.randomUUID());
    uacQidLink.setQid(qid);

    return saveAndRetrieveUacQidLink(uacQidLink);
  }

  private UacQidLink saveAndRetrieveUacQidLink(UacQidLink uacQidLink) {
    uacQidLinkRepository.saveAndFlush(uacQidLink);
    return uacQidLinkRepository
        .findById(uacQidLink.getId())
        .orElseThrow(() -> new RuntimeException("UacQidLink not found!"));
  }
}
