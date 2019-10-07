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
import uk.gov.ons.census.caseapisvc.model.dto.CaseContainerDTO;
import uk.gov.ons.census.caseapisvc.model.dto.QidDTO;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.model.entity.Event;
import uk.gov.ons.census.caseapisvc.model.entity.EventType;
import uk.gov.ons.census.caseapisvc.model.entity.UacQidLink;
import uk.gov.ons.census.caseapisvc.model.repository.CaseRepository;
import uk.gov.ons.census.caseapisvc.model.repository.UacQidLinkRepository;
import uk.gov.ons.census.caseapisvc.utility.DataUtils;

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

  private static final String TEST_REFERENCE_DOES_NOT_EXIST = "99999999";
  public static final String TEST_QID = "test_qid";
  public static final String ADDRESS_TYPE_TEST = "addressTypeTest";

  @LocalServerPort private int port;

  @Autowired private CaseRepository caseRepo;
  @Autowired private UacQidLinkRepository uacQidLinkRepository;

  private EasyRandom easyRandom;

  @Before
  public void setUp() {
    caseRepo.deleteAll();

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
    setupTestCcsUacQidLink(DataUtils.TEST_CCS_QID, ccsCase);

    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/ccs/%s/qid", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .asJson();

    QidDTO actualQidDTO =
        DataUtils.mapper.readValue(jsonResponse.getBody().getObject().toString(), QidDTO.class);
    assertThat(actualQidDTO.getQid()).isEqualTo(DataUtils.TEST_CCS_QID);
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
    setupTestCcsUacQidLink(TEST_CCS_QID, nonCcsCase);
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

    UacQidLink uacQidLink = easyRandom.nextObject(UacQidLink.class);
    uacQidLink.setActive(true);

    Event event = easyRandom.nextObject(Event.class);
    event.setCaze(null);
    event.setEventPayload(null);
    event.setEventType(EventType.CASE_CREATED);
    event.setUacQidLink(uacQidLink);
    event.setEventPayload("{}");

    uacQidLink.setCaze(caze);
    uacQidLink.setEvents(Collections.singletonList(event));

    caze.setUacQidLinks(Collections.singletonList(uacQidLink));

    caseRepo.saveAndFlush(caze);

    return caseRepo
        .findByCaseId(UUID.fromString(caseId))
        .orElseThrow(() -> new RuntimeException("Case not found!"));
  }

  private Case setupTestCaseWithoutEvents(String caseId) {
    return setupTestCaseWithoutEvents(caseId, false);
  }

  private Case setupTestCcsCaseWithoutEvents(String caseId) {
    return setupTestCaseWithoutEvents(caseId, true);
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

  private void setupTestCcsUacQidLink(String qid, Case caze) {
    UacQidLink uacQidLink = new UacQidLink();
    uacQidLink.setId(UUID.randomUUID());
    uacQidLink.setCaze(caze);
    uacQidLink.setQid(qid);
    uacQidLink.setCcsCase(true);

    uacQidLinkRepository.saveAndFlush(uacQidLink);
  }

  private Case setupTestCaseWithoutEvents(String caseId, boolean ccsCase) {
    Case caze = getaCase(caseId);
    caze.setCcsCase(ccsCase);

    caseRepo.saveAndFlush(caze);

    return caseRepo
        .findByCaseId(UUID.fromString(TEST_CASE_ID_1_EXISTS))
        .orElseThrow(() -> new RuntimeException("Case not found!"));
  }

  private String createUrl(String urlFormat, int port, String param1) {
    return String.format(urlFormat, port, param1);
  }
}
