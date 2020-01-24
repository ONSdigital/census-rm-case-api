package uk.gov.ons.census.caseapisvc.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.TEST_CCS_QID;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.extractCaseContainerDTOFromResponse;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.extractCaseContainerDTOsFromResponse;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.census.caseapisvc.model.dto.CaseContainerDTO;
import uk.gov.ons.census.caseapisvc.model.dto.QidDTO;
import uk.gov.ons.census.caseapisvc.model.dto.ResponseManagementEvent;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidDTO;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.model.entity.Event;
import uk.gov.ons.census.caseapisvc.model.entity.EventType;
import uk.gov.ons.census.caseapisvc.model.entity.UacQidLink;
import uk.gov.ons.census.caseapisvc.model.repository.CaseRepository;
import uk.gov.ons.census.caseapisvc.model.repository.EventRepository;
import uk.gov.ons.census.caseapisvc.model.repository.UacQidLinkRepository;
import uk.gov.ons.census.caseapisvc.utility.DataUtils;
import uk.gov.ons.census.caseapisvc.utility.RabbitQueueHelper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CaseEndpointIT {

  private static final String TEST_UPRN_EXISTS = "123456789012345";
  private static final String TEST_UPRN_DOES_NOT_EXIST = "999999999999999";

  private static final String TEST_CASE_ID_1_EXISTS = "c0d4f87d-9d19-4393-80c9-9eb94f69c460";
  private static final String TEST_CASE_ID_2_EXISTS = "3e948f6a-00bb-466d-88a7-b0990a827b53";

  private static final String TEST_CASE_ID_DOES_NOT_EXIST = "590179eb-f8ce-4e2d-8cb6-ca4013a2ccf0";
  private static final String TEST_INVALID_CASE_ID = "anything";

  private static final String TEST_HOUSEHOLD_ENGLAND_TREATMENT_CODE = "HH_XXXXXE";
  private static final String TEST_CE_ENGLAND_TREATMENT_CODE = "CE_XXXXXE";

  private static final String TEST_REFERENCE_DOES_NOT_EXIST = "99999999";
  private static final String TEST_QID = "test_qid";
  private static final String ADDRESS_TYPE_TEST = "addressTypeTest";

  @Value("${queueconfig.uac-qid-created-queue}")
  private String uacQidCreatedQueueName;

  @LocalServerPort private int port;

  @Autowired private CaseRepository caseRepo;
  @Autowired private UacQidLinkRepository uacQidLinkRepository;
  @Autowired private EventRepository eventRepository;
  @Autowired private RabbitQueueHelper rabbitQueueHelper;

  private EasyRandom easyRandom;

  private static final String caseFulfilmentsQueueName = "dummy.case.fulfilments";

  @Before
  @Transactional
  public void setUp() {
    eventRepository.deleteAllInBatch();
    uacQidLinkRepository.deleteAllInBatch();
    caseRepo.deleteAllInBatch();

    rabbitQueueHelper.purgeQueue(uacQidCreatedQueueName);

    this.easyRandom = new EasyRandom(new EasyRandomParameters().randomizationDepth(1));
  }

  @Test
  public void shouldRetrieveMultipleCasesWithEventsWhenSearchingByUPRN() throws Exception {
    createTwoTestCasesWithEvents();

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/uprn/%s", port, TEST_UPRN_EXISTS))
            .header("accept", "application/json")
            .queryString("caseEvents", "true")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    List<CaseContainerDTO> actualData = extractCaseContainerDTOsFromResponse(response);

    assertThat(actualData.size()).isEqualTo(2);

    CaseContainerDTO case1 = actualData.get(0);
    CaseContainerDTO case2 = actualData.get(1);

    assertThat(case1.getUprn()).isEqualTo(TEST_UPRN_EXISTS);
    assertThat(case1.getCaseEvents().size()).isEqualTo(1);

    assertThat(case2.getUprn()).isEqualTo(TEST_UPRN_EXISTS);
    assertThat(case2.getCaseEvents().size()).isEqualTo(1);
  }

  @Test
  public void shouldRetrieveMultipleCasesWithoutEventsWhenSearchingByUPRN() throws Exception {
    createTwoTestCasesWithoutEvents();

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/uprn/%s", port, TEST_UPRN_EXISTS))
            .header("accept", "application/json")
            .queryString("caseEvents", "false")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    List<CaseContainerDTO> actualData = extractCaseContainerDTOsFromResponse(response);

    assertThat(actualData.size()).isEqualTo(2);

    CaseContainerDTO case1 = actualData.get(0);
    CaseContainerDTO case2 = actualData.get(1);

    assertThat(case1.getUprn()).isEqualTo(TEST_UPRN_EXISTS);
    assertThat(case1.getCaseEvents().size()).isEqualTo(0);

    assertThat(case2.getUprn()).isEqualTo(TEST_UPRN_EXISTS);
    assertThat(case2.getCaseEvents().size()).isEqualTo(0);
  }

  @Test
  public void shouldRetrieveMultipleCasesWithoutEventsByDefaultWhenSearchingByUPRN()
      throws Exception {
    createTwoTestCasesWithoutEvents();

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/uprn/%s", port, TEST_UPRN_EXISTS))
            .header("accept", "application/json")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    List<CaseContainerDTO> actualData = extractCaseContainerDTOsFromResponse(response);

    assertThat(actualData.size()).isEqualTo(2);

    CaseContainerDTO case1 = actualData.get(0);
    CaseContainerDTO case2 = actualData.get(1);

    assertThat(case1.getUprn()).isEqualTo(TEST_UPRN_EXISTS);
    assertThat(case1.getCaseEvents().size()).isEqualTo(0);

    assertThat(case2.getUprn()).isEqualTo(TEST_UPRN_EXISTS);
    assertThat(case2.getCaseEvents().size()).isEqualTo(0);
  }

  @Test
  public void shouldReturn404WhenUPRNNotFound() throws Exception {
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/uprn/%s", port, TEST_UPRN_DOES_NOT_EXIST))
            .header("accept", "application/json")
            .asJson();

    assertThat(jsonResponse.getStatus()).isEqualTo(NOT_FOUND.value());
  }

  @Test
  public void shouldRetrieveACaseWithEventsWhenSearchingByCaseId() throws Exception {
    createOneTestCaseWithEvent();

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/%s", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .queryString("caseEvents", "true")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    CaseContainerDTO actualData = extractCaseContainerDTOFromResponse(response);

    assertThat(actualData.getCaseId()).isEqualTo(TEST_CASE_ID_1_EXISTS);
    assertThat(actualData.getCaseEvents().size()).isEqualTo(1);
  }

  @Test
  public void shouldRetrieveACaseWithoutEventsWhenSearchingByCaseId() throws Exception {
    createOneTestCaseWithoutEvents();

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/%s", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .queryString("caseEvents", "false")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    CaseContainerDTO actualData = extractCaseContainerDTOFromResponse(response);

    assertThat(actualData.getCaseId()).isEqualTo(TEST_CASE_ID_1_EXISTS);
    assertThat(actualData.getCaseEvents().size()).isEqualTo(0);
  }

  @Test
  public void shouldRetrieveACaseWithoutEventsByDefaultWhenSearchingByCaseId() throws Exception {
    createOneTestCaseWithoutEvents();

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/%s", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    CaseContainerDTO actualData = extractCaseContainerDTOFromResponse(response);

    assertThat(actualData.getCaseId()).isEqualTo(TEST_CASE_ID_1_EXISTS);
    assertThat(actualData.getCaseEvents().size()).isEqualTo(0);
  }

  @Test
  public void shouldReturn404WhenCaseIdNotFound() throws UnirestException {
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/%s", port, TEST_CASE_ID_DOES_NOT_EXIST))
            .header("accept", "application/json")
            .asJson();

    assertThat(jsonResponse.getStatus()).isEqualTo(NOT_FOUND.value());
  }

  @Test
  public void shouldReturn404WhenInvalidCaseId() throws UnirestException {
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/%s", port, TEST_INVALID_CASE_ID))
            .header("accept", "application/json")
            .asJson();

    assertThat(jsonResponse.getStatus()).isEqualTo(NOT_FOUND.value());
  }

  @Test
  public void shouldRetrieveACaseWithEventsWhenSearchingByCaseReference() throws Exception {
    Case expectedCase = createOneTestCaseWithEvent();
    String expectedCaseRef = Integer.toString(expectedCase.getCaseRef());

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/ref/%s", port, expectedCaseRef))
            .header("accept", "application/json")
            .queryString("caseEvents", "true")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    CaseContainerDTO actualData = extractCaseContainerDTOFromResponse(response);

    assertThat(actualData.getCaseRef()).isEqualTo(expectedCaseRef);
    assertThat(actualData.getCaseEvents().size()).isEqualTo(1);
  }

  @Test
  public void shouldRetrieveACaseWithoutEventsWhenSearchingByCaseReference() throws Exception {
    Case expectedCase = createOneTestCaseWithoutEvents();
    String expectedCaseRef = Integer.toString(expectedCase.getCaseRef());

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/ref/%s", port, expectedCaseRef))
            .header("accept", "application/json")
            .queryString("caseEvents", "false")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    CaseContainerDTO actualData = extractCaseContainerDTOFromResponse(response);

    assertThat(actualData.getCaseRef()).isEqualTo(expectedCaseRef);
    assertThat(actualData.getCaseEvents().size()).isEqualTo(0);
  }

  @Test
  public void shouldRetrieveACaseWithoutEventsByDefaultWhenSearchingByCaseReference()
      throws Exception {
    Case expectedCase = createOneTestCaseWithoutEvents();
    String expectedCaseRef = Integer.toString(expectedCase.getCaseRef());

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/ref/%s", port, expectedCaseRef))
            .header("accept", "application/json")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    CaseContainerDTO actualData = extractCaseContainerDTOFromResponse(response);

    assertThat(actualData.getCaseRef()).isEqualTo(expectedCaseRef);
    assertThat(actualData.getCaseEvents().size()).isEqualTo(0);
  }

  @Test
  public void shouldReturn404WhenCaseReferenceNotFound() throws Exception {
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(
                createUrl("http://localhost:%d/cases/ref/%s", port, TEST_REFERENCE_DOES_NOT_EXIST))
            .header("accept", "application/json")
            .asJson();

    assertThat(jsonResponse.getStatus()).isEqualTo(NOT_FOUND.value());
  }

  @Test
  public void testCorrectCaseReturnedWhenRequestedByQid() throws UnirestException, IOException {
    Case caze = setupTestCaseWithoutEvents(TEST_CASE_ID_1_EXISTS);
    setupTestUacQidLink(TEST_QID, caze);

    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/qid/%s", port, TEST_QID))
            .header("accept", "application/json")
            .asJson();

    CaseContainerDTO caseContainerDTO = DataUtils.extractCaseIdDtoFromResponse(jsonResponse);
    assertThat(caseContainerDTO.getCaseId()).isEqualTo(TEST_CASE_ID_1_EXISTS);
    assertThat(caseContainerDTO.getAddressType()).isEqualTo(ADDRESS_TYPE_TEST);
  }

  @Test
  public void testCorrectCcsQidReturnedWhenRequestedByCaseId()
      throws UnirestException, IOException {
    Case ccsCase = setupTestCcsCaseWithoutEvents(TEST_CASE_ID_1_EXISTS);
    setupTestCcsUacQidLink(DataUtils.TEST_CCS_QID, ccsCase, true);

    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/ccs/%s/qid", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .asJson();

    QidDTO actualQidDTO =
        DataUtils.mapper.readValue(jsonResponse.getBody().getObject().toString(), QidDTO.class);
    assertThat(actualQidDTO.getQuestionnaireId()).isEqualTo(DataUtils.TEST_CCS_QID);
    assertThat(actualQidDTO.isActive()).isTrue();
  }

  @Test
  public void testCorrectInactiveCcsQidReturnedWhenRequestedByCaseId()
      throws UnirestException, IOException {
    Case ccsCase = setupTestCcsCaseWithoutEvents(TEST_CASE_ID_1_EXISTS);
    setupTestCcsUacQidLink(DataUtils.TEST_CCS_QID, ccsCase, false);

    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/ccs/%s/qid", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .asJson();

    QidDTO actualQidDTO =
        DataUtils.mapper.readValue(jsonResponse.getBody().getObject().toString(), QidDTO.class);
    assertThat(actualQidDTO.getQuestionnaireId()).isEqualTo(DataUtils.TEST_CCS_QID);
    assertThat(actualQidDTO.isActive()).isFalse();
  }

  @Test
  public void testShouldReturn404WhenCcsCaseNotFound() throws UnirestException {
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(
                createUrl(
                    "http://localhost:%d/cases/ccs/%s/qid", port, TEST_CASE_ID_DOES_NOT_EXIST))
            .header("accept", "application/json")
            .asJson();

    assertThat(jsonResponse.getStatus()).isEqualTo(NOT_FOUND.value());
  }

  @Test
  public void testShouldReturn404WhenCaseIsNotCcsWithCcsQid() throws UnirestException {
    Case nonCcsCase = setupTestCaseWithoutEvents(TEST_CASE_ID_1_EXISTS);
    setupTestCcsUacQidLink(TEST_CCS_QID, nonCcsCase, true);
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/ccs/%s/qid", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .asJson();

    assertThat(jsonResponse.getStatus()).isEqualTo(NOT_FOUND.value());
  }

  @Test
  public void testShouldReturn404WhenCcsCaseExistsWithNoCcsQid() throws UnirestException {
    Case ccsCase = setupTestCcsCaseWithoutEvents(TEST_CASE_ID_1_EXISTS);
    setupTestUacQidLink(TEST_QID, ccsCase);
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/ccs/%s/qid", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .asJson();

    assertThat(jsonResponse.getStatus()).isEqualTo(NOT_FOUND.value());
  }

  @Test
  public void testShouldReturn404CcsCaseExistsWithNoQids() throws UnirestException {
    Case ccsCase = setupTestCcsCaseWithoutEvents(TEST_CASE_ID_1_EXISTS);
    setupTestUacQidLink(TEST_QID, ccsCase);
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/ccs/%s/qid", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .asJson();

    assertThat(jsonResponse.getStatus()).isEqualTo(NOT_FOUND.value());
  }

  @Test
  public void testGetNewUacQidForEnglishHouseholdCase() throws UnirestException, IOException {
    // Given
    setupUnitTestCaseWithTreatmentCode(
        TEST_CASE_ID_1_EXISTS, TEST_HOUSEHOLD_ENGLAND_TREATMENT_CODE);

    // When
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/%s/qid", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .asJson();

    // Then
    UacQidDTO actualUacQidDTO =
        DataUtils.mapper.readValue(jsonResponse.getBody().getObject().toString(), UacQidDTO.class);
    assertThat(actualUacQidDTO.getQuestionnaireId()).startsWith("01");
    assertThat(actualUacQidDTO.getUac()).isNotNull();
  }

  @Test
  public void testGetNewUacQidForEnglishCeUnitCase() throws UnirestException, IOException {
    // Given
    setupUnitTestCaseWithTreatmentCode(TEST_CASE_ID_1_EXISTS, TEST_CE_ENGLAND_TREATMENT_CODE);

    // When
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/%s/qid", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .asJson();

    // Then
    UacQidDTO actualUacQidDTO =
        DataUtils.mapper.readValue(jsonResponse.getBody().getObject().toString(), UacQidDTO.class);
    assertThat(actualUacQidDTO.getQuestionnaireId()).startsWith("21");
    assertThat(actualUacQidDTO.getUac()).isNotNull();
  }

  @Test
  @DirtiesContext
  public void testGetNewUacQidForCaseDistributesUacCreatedEvent()
      throws UnirestException, IOException, InterruptedException {
    // Given
    Case caze =
        setupUnitTestCaseWithTreatmentCode(
            TEST_CASE_ID_1_EXISTS, TEST_HOUSEHOLD_ENGLAND_TREATMENT_CODE);
    BlockingQueue<String> uacQidCreatedQueue = rabbitQueueHelper.listen(uacQidCreatedQueueName);

    // When
    Unirest.get(createUrl("http://localhost:%d/cases/%s/qid", port, TEST_CASE_ID_1_EXISTS))
        .header("accept", "application/json")
        .asJson();

    // Then
    String message = rabbitQueueHelper.checkExpectedMessageReceived(uacQidCreatedQueue);
    ResponseManagementEvent responseManagementEvent =
        DataUtils.mapper.readValue(message, ResponseManagementEvent.class);

    assertThat(responseManagementEvent.getPayload().getUacQidCreated().getCaseId())
        .isEqualTo(caze.getCaseId().toString());
    assertThat(responseManagementEvent.getPayload().getUacQidCreated().getQid()).startsWith("01");
    assertThat(responseManagementEvent.getPayload().getUacQidCreated().getUac()).isNotNull();
    assertThat(responseManagementEvent.getEvent().getSource()).isEqualTo("RESPONSE_MANAGEMENT");
    assertThat(responseManagementEvent.getEvent().getChannel()).isEqualTo("RM");
    assertThat(responseManagementEvent.getEvent().getType()).isEqualTo("RM_UAC_CREATED");
  }

  @Test
  public void testGetNewUacQidForCaseDoesNotReturnTheSameQidUacTwice()
      throws UnirestException, IOException {
    // Given
    setupUnitTestCaseWithTreatmentCode(
        TEST_CASE_ID_1_EXISTS, TEST_HOUSEHOLD_ENGLAND_TREATMENT_CODE);

    // When
    HttpResponse<JsonNode> firstJsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/%s/qid", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .asJson();
    UacQidDTO firstUacQidDTO =
        DataUtils.mapper.readValue(
            firstJsonResponse.getBody().getObject().toString(), UacQidDTO.class);

    HttpResponse<JsonNode> secondJsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/%s/qid", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .asJson();
    UacQidDTO secondUacQidDTO =
        DataUtils.mapper.readValue(
            secondJsonResponse.getBody().getObject().toString(), UacQidDTO.class);

    // Then
    assertThat(firstUacQidDTO.getQuestionnaireId())
        .isNotEqualTo(secondUacQidDTO.getQuestionnaireId());
    assertThat(firstUacQidDTO.getUac()).isNotEqualTo(secondUacQidDTO.getUac());
  }

  @Test
  public void testGetNewIndividualUacQidForEnglishHouseholdCase()
      throws UnirestException, IOException {
    // Given
    setupUnitTestCaseWithTreatmentCode(
        TEST_CASE_ID_1_EXISTS, TEST_HOUSEHOLD_ENGLAND_TREATMENT_CODE);
    UUID individualCaseId = UUID.randomUUID();

    // When
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(
                String.format(
                    "http://localhost:%d/cases/%s/qid?individual=true&individualCaseId=%s",
                    port, TEST_CASE_ID_1_EXISTS, individualCaseId.toString()))
            .header("accept", "application/json")
            .asJson();

    // Then
    UacQidDTO actualUacQidDTO =
        DataUtils.mapper.readValue(jsonResponse.getBody().getObject().toString(), UacQidDTO.class);
    assertThat(actualUacQidDTO.getQuestionnaireId()).startsWith("21");
    assertThat(actualUacQidDTO.getUac()).isNotNull();
  }

  @Test
  @DirtiesContext
  public void testGetNewIndividualUacQidForCaseDistributesUacCreatedEvent()
      throws UnirestException, IOException, InterruptedException {
    // Given
    setupUnitTestCaseWithTreatmentCode(
        TEST_CASE_ID_1_EXISTS, TEST_HOUSEHOLD_ENGLAND_TREATMENT_CODE);
    UUID individualCaseId = UUID.randomUUID();
    BlockingQueue<String> uacQidCreatedQueue = rabbitQueueHelper.listen(uacQidCreatedQueueName);

    // When
    Unirest.get(
            String.format(
                "http://localhost:%d/cases/%s/qid?individual=true&individualCaseId=%s",
                port, TEST_CASE_ID_1_EXISTS, individualCaseId.toString()))
        .header("accept", "application/json")
        .asJson();

    // Then
    String message = rabbitQueueHelper.checkExpectedMessageReceived(uacQidCreatedQueue);
    ResponseManagementEvent responseManagementEvent =
        DataUtils.mapper.readValue(message, ResponseManagementEvent.class);

    assertThat(responseManagementEvent.getPayload().getUacQidCreated().getCaseId())
        .isEqualTo(individualCaseId.toString());
    assertThat(responseManagementEvent.getPayload().getUacQidCreated().getQid()).startsWith("21");
    assertThat(responseManagementEvent.getPayload().getUacQidCreated().getUac()).isNotNull();
    assertThat(responseManagementEvent.getEvent().getSource()).isEqualTo("RESPONSE_MANAGEMENT");
    assertThat(responseManagementEvent.getEvent().getChannel()).isEqualTo("RM");
    assertThat(responseManagementEvent.getEvent().getType()).isEqualTo("RM_UAC_CREATED");
    assertThat(responseManagementEvent.getEvent().getTransactionId()).isNotNull();
    assertThat(responseManagementEvent.getEvent().getDateTime()).isNotNull();
  }

  @Test
  @DirtiesContext
  public void testGetNewIndividualUacQidForCaseDistributesIndividualCaseCreatedEvent()
      throws UnirestException, IOException, InterruptedException {
    // Given
    Case parentCase =
        setupUnitTestCaseWithTreatmentCode(
            TEST_CASE_ID_1_EXISTS, TEST_HOUSEHOLD_ENGLAND_TREATMENT_CODE);
    UUID individualCaseId = UUID.randomUUID();
    BlockingQueue<String> caseFulfilmentQueue = rabbitQueueHelper.listen(caseFulfilmentsQueueName);

    // When
    Unirest.get(
            String.format(
                "http://localhost:%d/cases/%s/qid?individual=true&individualCaseId=%s",
                port, TEST_CASE_ID_1_EXISTS, individualCaseId.toString()))
        .header("accept", "application/json")
        .asJson();

    // Then
    String message = rabbitQueueHelper.checkExpectedMessageReceived(caseFulfilmentQueue);
    ResponseManagementEvent responseManagementEvent =
        DataUtils.mapper.readValue(message, ResponseManagementEvent.class);

    assertThat(responseManagementEvent.getPayload().getFulfilmentRequest().getFulfilmentCode())
        .isEqualTo("RM_TC_HI");
    assertThat(responseManagementEvent.getPayload().getFulfilmentRequest().getCaseId())
        .isEqualTo(parentCase.getCaseId().toString());
    System.out.println("Comparing individual ids");
    assertThat(responseManagementEvent.getPayload().getFulfilmentRequest().getIndividualCaseId())
        .isEqualTo(individualCaseId.toString());
    assertThat(responseManagementEvent.getEvent().getSource()).isEqualTo("RESPONSE_MANAGEMENT");
    assertThat(responseManagementEvent.getEvent().getChannel()).isEqualTo("RM");
    assertThat(responseManagementEvent.getEvent().getType()).isEqualTo("FULFILMENT_REQUESTED");
    System.out.println("Comparing transactions");
    assertThat(responseManagementEvent.getEvent().getTransactionId()).isNotNull();
    assertThat(responseManagementEvent.getEvent().getDateTime()).isNotNull();
  }

  @Test
  public void testGetNewIndividualUacQidIndividualCaseIdAlreadyExists() throws UnirestException {
    // Given
    setupUnitTestCaseWithTreatmentCode(
        TEST_CASE_ID_1_EXISTS, TEST_HOUSEHOLD_ENGLAND_TREATMENT_CODE);
    UUID individualCaseId = UUID.randomUUID();
    setupTestCaseWithoutEvents(individualCaseId.toString());

    // When
    HttpResponse<String> response =
        Unirest.get(
                String.format(
                    "http://localhost:%d/cases/%s/qid?individual=true&individualCaseId=%s",
                    port, TEST_CASE_ID_1_EXISTS, individualCaseId.toString()))
            .asString();

    // Then
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  private Case createOneTestCaseWithEvent() {
    return setupTestCaseWithEvent(TEST_CASE_ID_1_EXISTS);
  }

  private Case createOneTestCaseWithoutEvents() {
    return setupTestCaseWithoutEvents(TEST_CASE_ID_1_EXISTS);
  }

  private void createTwoTestCasesWithEvents() {
    setupTestCaseWithEvent(TEST_CASE_ID_1_EXISTS);
    setupTestCaseWithEvent(TEST_CASE_ID_2_EXISTS);
  }

  private void createTwoTestCasesWithoutEvents() {
    setupTestCaseWithoutEvents(TEST_CASE_ID_1_EXISTS);
    setupTestCaseWithoutEvents(TEST_CASE_ID_2_EXISTS);
  }

  private Case setupTestCaseWithEvent(String caseId) {
    Case caze = easyRandom.nextObject(Case.class);
    caze.setCaseId(UUID.fromString(caseId));
    caze.setEvents(null);
    caze.setUprn(TEST_UPRN_EXISTS);
    caze.setReceiptReceived(false);

    UacQidLink uacQidLink = new UacQidLink();
    uacQidLink.setId(UUID.randomUUID());
    uacQidLink.setActive(true);

    Event event = easyRandom.nextObject(Event.class);
    event.setCaze(null);
    event.setEventPayload(null);
    event.setEventType(EventType.CASE_CREATED);
    event.setUacQidLink(uacQidLink);
    event.setEventPayload("{}");

    uacQidLink.setCaze(caze);
    uacQidLink.setEvents(Collections.singletonList(event));

    caseRepo.saveAndFlush(caze);
    uacQidLinkRepository.save(uacQidLink);

    return caseRepo
        .findByCaseId(UUID.fromString(caseId))
        .orElseThrow(() -> new RuntimeException("Case not found!"));
  }

  private Case setupTestCaseWithoutEvents(String caseId) {
    return setupTestCaseWithoutEvents(caseId, "CENSUS");
  }

  private Case setupTestCcsCaseWithoutEvents(String caseId) {
    return setupTestCaseWithoutEvents(caseId, "CCS");
  }

  private Case getaCase(String caseId) {
    Case caze = easyRandom.nextObject(Case.class);
    caze.setCaseId(UUID.fromString(caseId));
    caze.setEvents(null);
    caze.setUprn(TEST_UPRN_EXISTS);
    caze.setReceiptReceived(false);
    caze.setAddressType(ADDRESS_TYPE_TEST);

    caze.setUacQidLinks(null);
    return caze;
  }

  private void setupTestUacQidLink(String qid, Case caze) {
    UacQidLink uacQidLink = new UacQidLink();
    uacQidLink.setId(UUID.randomUUID());
    uacQidLink.setCaze(caze);
    uacQidLink.setQid(qid);

    uacQidLinkRepository.saveAndFlush(uacQidLink);
  }

  private void setupTestCcsUacQidLink(String qid, Case caze, boolean active) {
    UacQidLink uacQidLink = new UacQidLink();
    uacQidLink.setId(UUID.randomUUID());
    uacQidLink.setCaze(caze);
    uacQidLink.setQid(qid);
    uacQidLink.setCcsCase(true);
    uacQidLink.setActive(active);

    uacQidLinkRepository.saveAndFlush(uacQidLink);
  }

  private Case setupTestCaseWithoutEvents(String caseId, String survey) {
    Case caze = getaCase(caseId);
    caze.setSurvey(survey);

    return saveAndRetreiveCase(caze);
  }

  private Case setupUnitTestCaseWithTreatmentCode(String caseId, String treatmentCode) {
    Case caze = getaCase(caseId);
    caze.setTreatmentCode(treatmentCode);
    caze.setAddressLevel("U");

    return saveAndRetreiveCase(caze);
  }

  private Case saveAndRetreiveCase(Case caze) {
    caseRepo.saveAndFlush(caze);

    return caseRepo
        .findByCaseId(UUID.fromString(caze.getCaseId().toString()))
        .orElseThrow(() -> new RuntimeException("Case not found!"));
  }

  private String createUrl(String urlFormat, int port, String param1) {
    return String.format(urlFormat, port, param1);
  }
}
