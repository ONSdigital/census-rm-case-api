package uk.gov.ons.census.caseapisvc.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.TEST_CCS_QID;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.extractCaseContainerDTOFromResponse;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.extractCaseContainerDTOsFromResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.census.caseapisvc.model.dto.CCSLaunchDTO;
import uk.gov.ons.census.caseapisvc.model.dto.CaseContainerDTO;
import uk.gov.ons.census.caseapisvc.model.dto.ResponseManagementEvent;
import uk.gov.ons.census.caseapisvc.model.dto.TelephoneCaptureDTO;
import uk.gov.ons.census.caseapisvc.model.entity.*;
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

  private static final String TEST_REFERENCE_DOES_NOT_EXIST = "9999999999";
  private static final String TEST_QID = "test_qid";
  private static final String ADDRESS_TYPE_TEST = "addressTypeTest";

  private static final String TEST_POSTCODE_NO_SPACE = "AB12BC";
  private static final String TEST_POSTCODE_WITH_SPACE = "AB1 2BC";

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
    try {
      clearDown();
    } catch (Exception e) {
      // this is expected behaviour, where the event rows are deleted, then the case-processor image
      // puts a new
      // event row on and the case table clear down fails.  2nd run should clear it down
      clearDown();
    }

    rabbitQueueHelper.purgeQueue(caseFulfilmentsQueueName);

    easyRandom = new EasyRandom(new EasyRandomParameters().randomizationDepth(1));
  }

  public void clearDown() {
    eventRepository.deleteAllInBatch();
    uacQidLinkRepository.deleteAllInBatch();
    caseRepo.deleteAllInBatch();
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
  public void shouldOnlyRetrieveACaseWithValidAddressWhenSearchingByUPRN() throws Exception {
    setupTestCaseWithoutEvents(TEST_CASE_ID_1_EXISTS);
    setupTestCaseWithAddressInvalid(TEST_CASE_ID_2_EXISTS);

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/uprn/%s", port, TEST_UPRN_EXISTS))
            .header("accept", "application/json")
            .queryString("validAddressOnly", "true")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    List<CaseContainerDTO> actualData = extractCaseContainerDTOsFromResponse(response);

    assertThat(actualData.size()).isEqualTo(1);

    CaseContainerDTO case1 = actualData.get(0);
    assertThat(case1.getUprn()).isEqualTo(TEST_UPRN_EXISTS);
    assertThat(case1.isAddressInvalid()).isFalse();
  }

  @Test
  public void shouldRetrieveCaseCasesWhenSearchingByUPRNAndValidAddressOnlyFalse()
      throws Exception {
    setupTestCaseWithAddressInvalid(TEST_CASE_ID_1_EXISTS);

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/uprn/%s", port, TEST_UPRN_EXISTS))
            .header("accept", "application/json")
            .queryString("validAddressOnly", "false")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    List<CaseContainerDTO> actualData = extractCaseContainerDTOsFromResponse(response);

    assertThat(actualData.size()).isEqualTo(1);

    CaseContainerDTO case1 = actualData.get(0);
    assertThat(case1.getUprn()).isEqualTo(TEST_UPRN_EXISTS);
    assertThat(case1.isAddressInvalid()).isTrue();
  }

  @Test
  public void shouldReturn404WhenUPRNButAddressInvalid() throws Exception {
    setupTestCaseWithAddressInvalid(TEST_CASE_ID_1_EXISTS);

    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/uprn/%s", port, TEST_UPRN_EXISTS))
            .header("accept", "application/json")
            .queryString("validAddressOnly", "true")
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

    assertThat(actualData.getCaseId()).isEqualTo(UUID.fromString(TEST_CASE_ID_1_EXISTS));
    assertThat(actualData.getCaseEvents().size()).isEqualTo(1);
    assertThat(actualData.getSecureEstablishment()).isNull();
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

    assertThat(actualData.getCaseId()).isEqualTo(UUID.fromString(TEST_CASE_ID_1_EXISTS));
    assertThat(actualData.getCaseEvents().size()).isEqualTo(0);
    assertThat(actualData.getSecureEstablishment()).isNull();
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

    assertThat(actualData.getCaseId()).isEqualTo(UUID.fromString(TEST_CASE_ID_1_EXISTS));
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

    assertThat(jsonResponse.getStatus()).isEqualTo(BAD_REQUEST.value());
  }

  @Test
  public void shouldRetrieveACaseWithEventsWhenSearchingByCaseReference() throws Exception {
    Case expectedCase = createOneTestCaseWithEvent();
    String expectedCaseRef = Long.toString(expectedCase.getCaseRef());

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
    String expectedCaseRef = Long.toString(expectedCase.getCaseRef());

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
    String expectedCaseRef = Long.toString(expectedCase.getCaseRef());

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
    assertThat(caseContainerDTO.getCaseId()).isEqualTo(UUID.fromString(TEST_CASE_ID_1_EXISTS));
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

    CCSLaunchDTO actualCCSLaunchDTO =
        DataUtils.mapper.readValue(
            jsonResponse.getBody().getObject().toString(), CCSLaunchDTO.class);
    assertThat(actualCCSLaunchDTO.getQuestionnaireId()).isEqualTo(DataUtils.TEST_CCS_QID);
    assertThat(actualCCSLaunchDTO.isActive()).isTrue();
    assertThat(actualCCSLaunchDTO.getFormType()).isEqualTo("H");
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

    CCSLaunchDTO actualCCSLaunchDTO =
        DataUtils.mapper.readValue(
            jsonResponse.getBody().getObject().toString(), CCSLaunchDTO.class);
    assertThat(actualCCSLaunchDTO.getQuestionnaireId()).isEqualTo(DataUtils.TEST_CCS_QID);
    assertThat(actualCCSLaunchDTO.isActive()).isFalse();
    assertThat(actualCCSLaunchDTO.getFormType()).isEqualTo("H");
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
    TelephoneCaptureDTO actualTelephoneCaptureDTO =
        DataUtils.mapper.readValue(
            jsonResponse.getBody().getObject().toString(), TelephoneCaptureDTO.class);
    assertThat(actualTelephoneCaptureDTO.getQuestionnaireId()).startsWith("01");
    assertThat(actualTelephoneCaptureDTO.getUac()).isNotNull();
    assertThat(actualTelephoneCaptureDTO.getFormType()).isEqualTo("H");
    assertThat(actualTelephoneCaptureDTO.getQuestionnaireType()).isEqualTo("01");
  }

  @Test
  public void testGetNewIndividualUacQidForEnglishCeUnitCase()
      throws UnirestException, IOException {
    // Given
    setupCEUnitTestCaseWithTreatmentCode(TEST_CASE_ID_1_EXISTS, TEST_CE_ENGLAND_TREATMENT_CODE);

    // When
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(
                createUrl(
                    "http://localhost:%d/cases/%s/qid?individual=true",
                    port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .asJson();

    // Then
    TelephoneCaptureDTO actualTelephoneCaptureDTO =
        DataUtils.mapper.readValue(
            jsonResponse.getBody().getObject().toString(), TelephoneCaptureDTO.class);
    assertThat(actualTelephoneCaptureDTO.getQuestionnaireId()).startsWith("21");
    assertThat(actualTelephoneCaptureDTO.getUac()).isNotNull();
    assertThat(actualTelephoneCaptureDTO.getFormType()).isEqualTo("I");
    assertThat(actualTelephoneCaptureDTO.getQuestionnaireType()).isEqualTo("21");
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
    TelephoneCaptureDTO firstTelephoneCaptureDTO =
        DataUtils.mapper.readValue(
            firstJsonResponse.getBody().getObject().toString(), TelephoneCaptureDTO.class);

    HttpResponse<JsonNode> secondJsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/%s/qid", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .asJson();
    TelephoneCaptureDTO secondTelephoneCaptureDTO =
        DataUtils.mapper.readValue(
            secondJsonResponse.getBody().getObject().toString(), TelephoneCaptureDTO.class);

    // Then
    assertThat(firstTelephoneCaptureDTO.getQuestionnaireId())
        .isNotEqualTo(secondTelephoneCaptureDTO.getQuestionnaireId());
    assertThat(firstTelephoneCaptureDTO.getUac()).isNotEqualTo(secondTelephoneCaptureDTO.getUac());
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
    TelephoneCaptureDTO actualTelephoneCaptureDTO =
        DataUtils.mapper.readValue(
            jsonResponse.getBody().getObject().toString(), TelephoneCaptureDTO.class);
    assertThat(actualTelephoneCaptureDTO.getQuestionnaireId()).startsWith("21");
    assertThat(actualTelephoneCaptureDTO.getUac()).isNotNull();
    assertThat(actualTelephoneCaptureDTO.getFormType()).isEqualTo("I");
    assertThat(actualTelephoneCaptureDTO.getQuestionnaireType()).isEqualTo("21");
  }

  @Test
  @DirtiesContext
  public void testGetNewUacQidForCaseDistributesFulfilmentEvent()
      throws UnirestException, IOException, InterruptedException {
    // Given
    Case caze =
        setupUnitTestCaseWithTreatmentCode(
            TEST_CASE_ID_1_EXISTS, TEST_HOUSEHOLD_ENGLAND_TREATMENT_CODE);
    BlockingQueue<String> caseFulfilmentQueue = rabbitQueueHelper.listen(caseFulfilmentsQueueName);

    // When
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/%s/qid", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .asJson();

    // Then
    TelephoneCaptureDTO actualTelephoneCaptureDTO =
        DataUtils.mapper.readValue(
            jsonResponse.getBody().getObject().toString(), TelephoneCaptureDTO.class);
    assertThat(actualTelephoneCaptureDTO.getQuestionnaireId()).startsWith("01");
    assertThat(actualTelephoneCaptureDTO.getUac()).isNotNull();

    String message = rabbitQueueHelper.checkExpectedMessageReceived(caseFulfilmentQueue);
    ResponseManagementEvent responseManagementEvent =
        DataUtils.mapper.readValue(message, ResponseManagementEvent.class);

    assertThat(responseManagementEvent.getPayload().getFulfilmentRequest().getFulfilmentCode())
        .isEqualTo("RM_TC");
    assertThat(responseManagementEvent.getPayload().getFulfilmentRequest().getCaseId())
        .isEqualTo(caze.getCaseId());
    assertThat(responseManagementEvent.getPayload().getFulfilmentRequest().getIndividualCaseId())
        .isNull();

    assertThat(
            responseManagementEvent
                .getPayload()
                .getFulfilmentRequest()
                .getUacQidCreated()
                .getCaseId())
        .isEqualTo(caze.getCaseId());
    assertThat(
            responseManagementEvent.getPayload().getFulfilmentRequest().getUacQidCreated().getQid())
        .startsWith("01");
    assertThat(
            responseManagementEvent.getPayload().getFulfilmentRequest().getUacQidCreated().getUac())
        .isNotNull();

    assertThat(responseManagementEvent.getEvent().getSource()).isEqualTo("RESPONSE_MANAGEMENT");
    assertThat(responseManagementEvent.getEvent().getChannel()).isEqualTo("RM");
    assertThat(responseManagementEvent.getEvent().getType()).isEqualTo("FULFILMENT_REQUESTED");
    assertThat(responseManagementEvent.getEvent().getTransactionId()).isNotNull();
    assertThat(responseManagementEvent.getEvent().getDateTime()).isNotNull();
  }

  @Test
  @DirtiesContext
  public void testGetNewIndividualUacQidForCaseDistributesFulfilmentEvent()
      throws UnirestException, IOException, InterruptedException {
    // Given
    Case parentCase =
        setupUnitTestCaseWithTreatmentCode(
            TEST_CASE_ID_1_EXISTS, TEST_HOUSEHOLD_ENGLAND_TREATMENT_CODE);
    UUID individualCaseId = UUID.randomUUID();
    BlockingQueue<String> caseFulfilmentQueue = rabbitQueueHelper.listen(caseFulfilmentsQueueName);

    // When
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(
                String.format(
                    "http://localhost:%d/cases/%s/qid?individual=true&individualCaseId=%s",
                    port, TEST_CASE_ID_1_EXISTS, individualCaseId.toString()))
            .header("accept", "application/json")
            .asJson();

    // Then
    TelephoneCaptureDTO actualTelephoneCaptureDTO =
        DataUtils.mapper.readValue(
            jsonResponse.getBody().getObject().toString(), TelephoneCaptureDTO.class);
    assertThat(actualTelephoneCaptureDTO.getQuestionnaireId()).startsWith("21");
    assertThat(actualTelephoneCaptureDTO.getUac()).isNotNull();

    String message = rabbitQueueHelper.checkExpectedMessageReceived(caseFulfilmentQueue);
    ResponseManagementEvent responseManagementEvent =
        DataUtils.mapper.readValue(message, ResponseManagementEvent.class);

    assertThat(responseManagementEvent.getPayload().getFulfilmentRequest().getFulfilmentCode())
        .isEqualTo("RM_TC_HI");
    assertThat(responseManagementEvent.getPayload().getFulfilmentRequest().getCaseId())
        .isEqualTo(parentCase.getCaseId());
    assertThat(responseManagementEvent.getPayload().getFulfilmentRequest().getIndividualCaseId())
        .isEqualTo(individualCaseId);

    assertThat(
            responseManagementEvent
                .getPayload()
                .getFulfilmentRequest()
                .getUacQidCreated()
                .getCaseId())
        .isEqualTo(individualCaseId);
    assertThat(
            responseManagementEvent.getPayload().getFulfilmentRequest().getUacQidCreated().getQid())
        .startsWith("21");
    assertThat(
            responseManagementEvent.getPayload().getFulfilmentRequest().getUacQidCreated().getUac())
        .isNotNull();

    assertThat(responseManagementEvent.getEvent().getSource()).isEqualTo("RESPONSE_MANAGEMENT");
    assertThat(responseManagementEvent.getEvent().getChannel()).isEqualTo("RM");
    assertThat(responseManagementEvent.getEvent().getType()).isEqualTo("FULFILMENT_REQUESTED");
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

  @Test
  @DirtiesContext
  public void getIndividualUacQidForSPGUnitLevellCase()
      throws UnirestException, IOException, InterruptedException {
    // Given
    Case caze = setUpSPGUnitCaseWithTreatmentCode(TEST_CASE_ID_1_EXISTS, "SPG_XXXE");
    BlockingQueue<String> caseFulfilmentQueue = rabbitQueueHelper.listen(caseFulfilmentsQueueName);

    // When
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(
                String.format(
                    "http://localhost:%d/cases/%s/qid?individual=true",
                    port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .asJson();

    // Then
    TelephoneCaptureDTO actualTelephoneCaptureDTO =
        DataUtils.mapper.readValue(
            jsonResponse.getBody().getObject().toString(), TelephoneCaptureDTO.class);
    assertThat(actualTelephoneCaptureDTO.getQuestionnaireId()).startsWith("21");
    assertThat(actualTelephoneCaptureDTO.getUac()).isNotNull();

    String message = rabbitQueueHelper.checkExpectedMessageReceived(caseFulfilmentQueue);
    ResponseManagementEvent responseManagementEvent =
        DataUtils.mapper.readValue(message, ResponseManagementEvent.class);

    assertThat(responseManagementEvent.getPayload().getFulfilmentRequest().getFulfilmentCode())
        .isEqualTo("RM_TC");
    assertThat(responseManagementEvent.getPayload().getFulfilmentRequest().getCaseId())
        .isEqualTo(caze.getCaseId());
    assertThat(responseManagementEvent.getPayload().getFulfilmentRequest().getIndividualCaseId())
        .isNull();

    assertThat(
            responseManagementEvent
                .getPayload()
                .getFulfilmentRequest()
                .getUacQidCreated()
                .getCaseId())
        .isEqualTo(caze.getCaseId());
    assertThat(
            responseManagementEvent.getPayload().getFulfilmentRequest().getUacQidCreated().getQid())
        .startsWith("21");
    assertThat(
            responseManagementEvent.getPayload().getFulfilmentRequest().getUacQidCreated().getUac())
        .isNotNull();

    assertThat(responseManagementEvent.getEvent().getSource()).isEqualTo("RESPONSE_MANAGEMENT");
    assertThat(responseManagementEvent.getEvent().getChannel()).isEqualTo("RM");
    assertThat(responseManagementEvent.getEvent().getType()).isEqualTo("FULFILMENT_REQUESTED");
    assertThat(responseManagementEvent.getEvent().getTransactionId()).isNotNull();
    assertThat(responseManagementEvent.getEvent().getDateTime()).isNotNull();
  }

  @Test
  public void testGetNewCe1UacQidForEnglishCeEstabCase() throws UnirestException, IOException {
    // Given
    Case CeEstabCase = getaCase(TEST_CASE_ID_1_EXISTS);
    CeEstabCase.setTreatmentCode(TEST_CE_ENGLAND_TREATMENT_CODE);
    CeEstabCase.setAddressLevel("E");
    CeEstabCase.setRegion("E1000");
    CeEstabCase.setCaseType("CE");
    CeEstabCase = saveAndRetreiveCase(CeEstabCase);

    // When
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(String.format("http://localhost:%d/cases/%s/qid", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .asJson();

    // Then
    TelephoneCaptureDTO actualTelephoneCaptureDTO =
        DataUtils.mapper.readValue(
            jsonResponse.getBody().getObject().toString(), TelephoneCaptureDTO.class);
    assertThat(actualTelephoneCaptureDTO.getQuestionnaireId()).startsWith("31");
    assertThat(actualTelephoneCaptureDTO.getUac()).isNotNull();
    assertThat(actualTelephoneCaptureDTO.getFormType()).isEqualTo("C");
    assertThat(actualTelephoneCaptureDTO.getQuestionnaireType()).isEqualTo("31");
  }

  @Test
  public void testFindOneCcsCaseByPostCode() throws IOException, UnirestException {

    // Given
    Case ccsCase = setupCcsCaseWithPostcode(TEST_POSTCODE_NO_SPACE, TEST_CASE_ID_1_EXISTS);

    // Second case should not match postcode
    Case otherCase = getaCase(TEST_CASE_ID_2_EXISTS);
    otherCase.setPostcode("ZY12XW");

    // When
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(
                createUrl(
                    "http://localhost:%d/cases/ccs/postcode/%s", port, TEST_POSTCODE_NO_SPACE))
            .header("accept", "application/json")
            .asJson();

    // Then
    assertThat(jsonResponse.getBody().isArray()).isTrue();
    List<CaseContainerDTO> foundCases =
        DataUtils.mapper.readValue(
            jsonResponse.getBody().getArray().toString(),
            new TypeReference<List<CaseContainerDTO>>() {});

    assertThat(foundCases).hasSize(1);
    assertThat(foundCases.get(0).getCaseId()).isEqualTo(ccsCase.getCaseId());
  }

  @Test
  public void testFindMultipleCcsCasesByPostCode() throws IOException, UnirestException {

    // Given
    Case[] matchCases = setupCcsCasesWithPostcode(TEST_POSTCODE_NO_SPACE, 3);
    Set<UUID> expectedCaseIds =
        Arrays.stream(matchCases).map(c -> c.getCaseId()).collect(Collectors.toSet());

    // When
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(
                createUrl(
                    "http://localhost:%d/cases/ccs/postcode/%s", port, TEST_POSTCODE_NO_SPACE))
            .header("accept", "application/json")
            .asJson();

    // Then
    assertThat(jsonResponse.getBody().isArray()).isTrue();
    List<CaseContainerDTO> foundCases =
        DataUtils.mapper.readValue(
            jsonResponse.getBody().getArray().toString(),
            new TypeReference<List<CaseContainerDTO>>() {});

    assertThat(foundCases).hasSize(3);
    Set<UUID> actualCaseIds =
        foundCases.stream().map(CaseContainerDTO::getCaseId).collect(Collectors.toSet());
    assertThat(actualCaseIds).isEqualTo(expectedCaseIds);
  }

  @Test
  public void testFindCcsCaseByPostCodeIgnoresSpaceInParam() throws IOException, UnirestException {

    // Given
    Case ccsCase = setupCcsCaseWithPostcode(TEST_POSTCODE_NO_SPACE, TEST_CASE_ID_1_EXISTS);

    // When
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(
                createUrl(
                    "http://localhost:%d/cases/ccs/postcode/%s", port, TEST_POSTCODE_WITH_SPACE))
            .header("accept", "application/json")
            .asJson();

    // Then
    assertThat(jsonResponse.getBody().isArray()).isTrue();
    List<CaseContainerDTO> foundCases =
        DataUtils.mapper.readValue(
            jsonResponse.getBody().getArray().toString(),
            new TypeReference<List<CaseContainerDTO>>() {});

    assertThat(foundCases).hasSize(1);
    assertThat(foundCases.get(0).getCaseId()).isEqualTo(ccsCase.getCaseId());
  }

  @Test
  public void testFindCcsCaseByPostCodeIgnoresSpaceInData() throws IOException, UnirestException {

    // Given
    Case ccsCase = setupCcsCaseWithPostcode(TEST_POSTCODE_WITH_SPACE, TEST_CASE_ID_1_EXISTS);

    // When
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(
                createUrl(
                    "http://localhost:%d/cases/ccs/postcode/%s", port, TEST_POSTCODE_NO_SPACE))
            .header("accept", "application/json")
            .asJson();

    // Then
    assertThat(jsonResponse.getBody().isArray()).isTrue();
    List<CaseContainerDTO> foundCases =
        DataUtils.mapper.readValue(
            jsonResponse.getBody().getArray().toString(),
            new TypeReference<List<CaseContainerDTO>>() {});

    assertThat(foundCases).hasSize(1);
    assertThat(foundCases.get(0).getCaseId()).isEqualTo(ccsCase.getCaseId());
  }

  @Test
  public void testFindCcsCaseByPostCodeIgnoresCharacterCase() throws IOException, UnirestException {

    // Given
    Case ccsCase = setupCcsCaseWithPostcode("aB12Cd", TEST_CASE_ID_1_EXISTS);

    // When
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/ccs/postcode/%s", port, "Ab12cD"))
            .header("accept", "application/json")
            .asJson();

    // Then
    assertThat(jsonResponse.getBody().isArray()).isTrue();
    List<CaseContainerDTO> foundCases =
        DataUtils.mapper.readValue(
            jsonResponse.getBody().getArray().toString(),
            new TypeReference<List<CaseContainerDTO>>() {});

    assertThat(foundCases).hasSize(1);
    assertThat(foundCases.get(0).getCaseId()).isEqualTo(ccsCase.getCaseId());
  }

  @Test
  public void shouldReturnCeSecureStatusTrue() throws Exception {

    setupTestCaseWithMetadata(TEST_CASE_ID_1_EXISTS, true);

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/%s", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .queryString("caseEvents", "false")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    CaseContainerDTO actualData = extractCaseContainerDTOFromResponse(response);

    assertThat(actualData.getCaseId()).isEqualTo(UUID.fromString(TEST_CASE_ID_1_EXISTS));
    assertThat(actualData.getCaseEvents().size()).isEqualTo(0);
    assertThat(actualData.getSecureEstablishment()).isTrue();
  }

  @Test
  public void shouldReturnCeSecureStatusFalse() throws Exception {

    setupTestCaseWithMetadata(TEST_CASE_ID_1_EXISTS, false);

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/%s", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .queryString("caseEvents", "false")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    CaseContainerDTO actualData = extractCaseContainerDTOFromResponse(response);

    assertThat(actualData.getCaseId()).isEqualTo(UUID.fromString(TEST_CASE_ID_1_EXISTS));
    assertThat(actualData.getCaseEvents().size()).isEqualTo(0);
    assertThat(actualData.getSecureEstablishment()).isFalse();
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
    caseRepo.saveAndFlush(caze);

    UacQidLink uacQidLink = new UacQidLink();
    uacQidLink.setId(UUID.randomUUID());
    uacQidLink.setActive(true);
    uacQidLink.setCaze(caze);
    uacQidLinkRepository.save(uacQidLink);

    Event event = new Event();
    event.setId(UUID.randomUUID());
    event.setCaze(null);
    event.setEventType(EventType.CASE_CREATED);
    event.setUacQidLink(uacQidLink);
    event.setEventPayload("{}");

    eventRepository.save(event);

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

  private Case setupTestCaseWithMetadata(String caseId, boolean secureEstablishment) {
    Case caze = getaCase(caseId);
    CaseMetadata metadata = new CaseMetadata();
    metadata.setSecureEstablishment(secureEstablishment);
    caze.setMetadata(metadata);
    caze.setCaseType("CE");

    return saveAndRetreiveCase(caze);
  }

  private Case setupTestCaseWithAddressInvalid(String caseId) {
    Case caze = getaCase(caseId);
    caze.setAddressInvalid(true);

    return saveAndRetreiveCase(caze);
  }

  private Case setupUnitTestCaseWithTreatmentCode(String caseId, String treatmentCode) {
    Case caze = getaCase(caseId);
    caze.setCaseType("HH");
    caze.setTreatmentCode(treatmentCode);
    caze.setRegion("E1000");
    caze.setAddressLevel("U");

    return saveAndRetreiveCase(caze);
  }

  private Case setupCEUnitTestCaseWithTreatmentCode(String caseId, String treatmentCode) {
    Case caze = getaCase(caseId);
    caze.setCaseType("CE");
    caze.setTreatmentCode(treatmentCode);
    caze.setRegion("E1000");
    caze.setAddressLevel("U");

    return saveAndRetreiveCase(caze);
  }

  private Case setUpSPGUnitCaseWithTreatmentCode(String caseId, String treatmentCode) {
    Case caze = getaCase(caseId);
    caze.setTreatmentCode(treatmentCode);
    caze.setRegion("E1000");
    caze.setAddressLevel("U");
    caze.setCaseType("SPG");

    return saveAndRetreiveCase(caze);
  }

  private Case setupCcsCaseWithPostcode(String postcode, String caseId) {
    Case ccsCase = getaCase(caseId);
    ccsCase.setSurvey("CCS");
    ccsCase.setPostcode(postcode);
    return saveAndRetreiveCase(ccsCase);
  }

  private Case[] setupCcsCasesWithPostcode(String postcode, int numberOfCases) {
    Case[] createdCases = new Case[numberOfCases];
    for (int i = 0; i < numberOfCases; i++) {
      Case newCase = setupCcsCaseWithPostcode(postcode, UUID.randomUUID().toString());
      createdCases[i] = newCase;
    }
    return createdCases;
  }

  private Case saveAndRetreiveCase(Case caze) {
    caseRepo.saveAndFlush(caze);

    return caseRepo
        .findByCaseId(caze.getCaseId())
        .orElseThrow(() -> new RuntimeException("Case not found!"));
  }

  private String createUrl(String urlFormat, int port, String param1) {
    return String.format(urlFormat, port, param1);
  }
}
