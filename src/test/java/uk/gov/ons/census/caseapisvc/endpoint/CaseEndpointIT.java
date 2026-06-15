package uk.gov.ons.census.caseapisvc.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.ons.census.caseapisvc.testutils.DataUtils.*;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.census.caseapisvc.model.dto.CaseContainerDTO;
import uk.gov.ons.census.caseapisvc.model.dto.CaseDetailsDTO;
import uk.gov.ons.census.caseapisvc.model.repository.*;
import uk.gov.ons.census.common.model.entity.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CaseEndpointIT {

  private static final Logger log = LoggerFactory.getLogger(CaseEndpointIT.class);

  private static final String TEST_UPRN = "123456789012345";
  private static final String TEST_UPRN_EXISTS = "123456789012345";
  private static final String TEST_UPRN_DOES_NOT_EXIST = "999999999999999";

  private static final String TEST_CASE_ID_1_EXISTS = "c0d4f87d-9d19-4393-80c9-9eb94f69c460";
  private static final String TEST_CASE_ID_2_EXISTS = "3e948f6a-00bb-466d-88a7-b0990a827b53";

  private static final String TEST_CASE_ID_DOES_NOT_EXIST = "590179eb-f8ce-4e2d-8cb6-ca4013a2ccf1";
  private static final String TEST_INVALID_CASE_ID = "anything";

  private static final String TEST_REFERENCE_DOES_NOT_EXIST = "9999999999";
  // private static final String ADDRESS_TYPE_TEST = "addressTypeTest";
  private static final String TEST_TOWN = "Tenby";
  private static final String TEST_POSTCODE_NO_SPACE = "AB12BC";
  private static final String TEST_POSTCODE_WITH_SPACE = "AB1 2BC";

  private static final String ADDRESS_TYPE_TEST = "addressTypeTest";

  @LocalServerPort private int port;

  @Autowired private CaseRepository caseRepository;
  @Autowired private EventRepository eventRepository;
  @Autowired private CollectionExerciseRepository collectionExerciseRepository;
  @Autowired private SurveyRepository surveyRepository;
  @Autowired private UacQidLinkRepository uacQidLinkRepository;

  private EasyRandom easyRandom;

  @BeforeEach
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

    easyRandom = new EasyRandom(new EasyRandomParameters().randomizationDepth(1));
  }

  public void clearDown() {
    eventRepository.deleteAllInBatch();
    uacQidLinkRepository.deleteAllInBatch();
    caseRepository.deleteAllInBatch();
    collectionExerciseRepository.deleteAllInBatch();
    surveyRepository.deleteAllInBatch();
  }

  @Test
  public void shouldRetrieveMultipleCasesWithEventsWhenSearchingByUPRN() throws Exception {
    // createTwoTestCasesWithEvents();
    setupTestCaseWithEvent(String.valueOf(UUID.randomUUID()));
    setupTestCaseWithEvent(String.valueOf(UUID.randomUUID()));

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
  public void getCaseByIdMinusEvents() {
    setupTestCaseWithEvent(TEST_CASE_ID_1_EXISTS);

    RestTemplate restTemplate = new RestTemplate();
    String url = "http://localhost:" + port + "/cases/" + TEST_CASE_ID_1_EXISTS;
    ResponseEntity<Case> foundCaseResponse = restTemplate.getForEntity(url, Case.class);

    Case actualCase = foundCaseResponse.getBody();
    assertThat(actualCase.getId()).isEqualTo(UUID.fromString(TEST_CASE_ID_1_EXISTS));
  }

  @Test
  public void shouldRetrieveACaseWithEventsWhenSearchingByCaseId() throws Exception {
    setupTestCaseWithEvent(TEST_CASE_ID_1_EXISTS);

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/%s", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .queryString("caseEvents", "true")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    CaseContainerDTO actualData = extractCaseContainerDTOFromResponse(response);

    assertThat(actualData.getCaseId()).isEqualTo(UUID.fromString(TEST_CASE_ID_1_EXISTS));
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

    assertThat(actualData.getCaseId()).isEqualTo(UUID.fromString(TEST_CASE_ID_1_EXISTS));
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
  public void shouldReturn400WhenInvalidCaseId() throws UnirestException {
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/%s", port, TEST_INVALID_CASE_ID))
            .header("accept", "application/json")
            .asJson();

    assertThat(jsonResponse.getStatus()).isEqualTo(BAD_REQUEST.value());
  }

  @Test
  public void shouldRetrieveACaseWithEventsWhenSearchingByCaseReference() throws Exception {
    Case expectedCase = setupTestCaseWithEvent(TEST_CASE_ID_1_EXISTS);
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
    Case expectedCase = setupTestCaseWithoutEvents(String.valueOf(UUID.randomUUID()));
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
  public void getCasesByPostcode() throws IOException, UnirestException {
    // createTwoTestCasesWithEvents();
    String case_1 = String.valueOf(UUID.randomUUID());
    setupTestCaseWithEvent(case_1);
    String case_2 = String.valueOf(UUID.randomUUID());
    setupTestCaseWithEvent(case_2);
    Optional<Case> caseObj1 =
        Optional.of(
            caseRepository
                .findById(UUID.fromString(case_1))
                .orElseThrow(() -> new RuntimeException("Case not found!")));

    Optional<Case> caseObj2 =
        Optional.of(
            caseRepository
                .findById(UUID.fromString(case_2))
                .orElseThrow(() -> new RuntimeException("Case not found!")));

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/postcode/%s", port, TEST_POSTCODE))
            .header("accept", "application/json")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    List<CaseContainerDTO> actualData = extractCaseContainerDTOsFromResponse(response);

    assertThat(actualData.size()).isEqualTo(2);

    CaseContainerDTO case1 = actualData.get(0);
    CaseContainerDTO case2 = actualData.get(1);

    assertThat(case1.getPostcode()).isEqualTo(TEST_POSTCODE);
    assertThat(case2.getPostcode()).isEqualTo(TEST_POSTCODE);
  }

  @Test
  public void getAllCaseDetails() throws IOException, UnirestException {
    Case caze = createOneTestCaseWithEvent();

    HttpResponse<JsonNode> response =
        Unirest.get(
                createUrl("http://localhost:%d/cases/case-details/%s", port, TEST_CASE_ID_1_EXISTS))
            .header("accept", "application/json")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    CaseDetailsDTO actualCaseDetails = extractCaseDetailsDTOsFromResponse(response);

    assertThat(actualCaseDetails.getCaseId()).isEqualTo(caze.getId());
  }

  public static CaseDetailsDTO extractCaseDetailsDTOsFromResponse(HttpResponse<JsonNode> response)
      throws IOException {
    return mapper.readValue(response.getBody().getObject().toString(), CaseDetailsDTO.class);
  }

  private Case createOneTestCaseWithoutEvents() {
    return setupTestCaseWithoutEvents(TEST_CASE_ID_1_EXISTS);
  }

  private void createTwoTestCasesWithEvents() {
    setupTestCaseWithEvent(TEST_CASE_ID_1_EXISTS);
    setupTestCaseWithEvent(TEST_CASE_ID_2_EXISTS);
  }

  private Case createOneTestCaseWithEvent() {
    return setupTestCaseWithEvent(TEST_CASE_ID_1_EXISTS);
  }

  private void createTwoTestCasesWithoutEvents() {
    setupTestCaseWithoutEvents(TEST_CASE_ID_1_EXISTS);
    setupTestCaseWithoutEvents(TEST_CASE_ID_2_EXISTS);
  }

  private Case setupTestCaseWithEvent(String caseId) {

    Survey junkSurvey = new Survey();
    junkSurvey.setId(UUID.randomUUID());
    junkSurvey.setName("Junk survey");
    junkSurvey.setSampleSeparator('j');
    surveyRepository.saveAndFlush(junkSurvey);

    CollectionExercise junkCollectionExercise = new CollectionExercise();
    junkCollectionExercise.setId(UUID.randomUUID());
    junkCollectionExercise.setName("Junk collex");
    junkCollectionExercise.setSurvey(junkSurvey);
    junkCollectionExercise.setReference("MVP012021");
    junkCollectionExercise.setStartDate(OffsetDateTime.now());
    junkCollectionExercise.setEndDate(OffsetDateTime.now().plusDays(2));
    junkCollectionExercise.setMetadata(null);
    collectionExerciseRepository.saveAndFlush(junkCollectionExercise);

    Case caze = easyRandom.nextObject(Case.class);
    caze.setId(UUID.fromString(caseId));
    caze.setEvents(null);
    caze.setUprn(TEST_UPRN_EXISTS);
    caze.setReceiptReceived(false);
    caze.setPostcode(TEST_POSTCODE);
    caze.setCollectionExercise(junkCollectionExercise);
    caseRepository.saveAndFlush(caze);

    UacQidLink uacQidLink = new UacQidLink();
    uacQidLink.setId(UUID.randomUUID());
    uacQidLink.setActive(true);
    uacQidLink.setQid(easyRandom.nextObject(String.class));
    uacQidLink.setUac("test_uac_1");
    uacQidLink.setUacHash("fakeHash_1");
    uacQidLink.setCaze(caze);
    uacQidLinkRepository.save(uacQidLink);

    caze =
        caseRepository
            .findById(UUID.fromString(caseId))
            .orElseThrow(() -> new RuntimeException("Case not found!"));
    caze.setUacQidLinks(List.of(uacQidLink));
    caseRepository.saveAndFlush(caze);

    Event event = new Event();
    event.setId(UUID.randomUUID());
    event.setCaze(null);
    event.setType(EventType.NEW_CASE);
    event.setUacQidLink(uacQidLink);
    event.setChannel("RM");
    event.setCorrelationId(UUID.randomUUID());
    event.setPayload("{}");
    event.setDescription("description");
    event.setMessageId(UUID.randomUUID());
    event.setCreatedBy("");
    event.setMessageTimestamp(OffsetDateTime.now());
    event.setProcessedAt(OffsetDateTime.now());
    event.setSource("");
    event.setDateTime(OffsetDateTime.now());

    eventRepository.save(event);

    return caseRepository
        .findById(UUID.fromString(caseId))
        .orElseThrow(() -> new RuntimeException("Case not found!"));
  }

  private void setupTestUacQidLink(String qid, Case caze) {
    UacQidLink uacQidLink = new UacQidLink();
    uacQidLink.setId(UUID.randomUUID());
    uacQidLink.setCaze(caze);
    uacQidLink.setQid(qid);

    uacQidLinkRepository.saveAndFlush(uacQidLink);
  }

  private Case setupTestCaseWithoutEvents(String id) {
    Case caze = getACase(id);
    Survey junkSurvey = new Survey();
    junkSurvey.setId(UUID.randomUUID());
    junkSurvey.setName("Junk survey");
    junkSurvey.setSampleSeparator('j');
    surveyRepository.saveAndFlush(junkSurvey);

    CollectionExercise junkCollectionExercise = new CollectionExercise();
    junkCollectionExercise.setId(UUID.randomUUID());
    junkCollectionExercise.setName("Junk collex");
    junkCollectionExercise.setSurvey(junkSurvey);
    junkCollectionExercise.setReference("MVP012021");
    junkCollectionExercise.setStartDate(OffsetDateTime.now());
    junkCollectionExercise.setEndDate(OffsetDateTime.now().plusDays(2));
    junkCollectionExercise.setMetadata(null);
    collectionExerciseRepository.saveAndFlush(junkCollectionExercise);

    caze.setCollectionExercise(junkCollectionExercise);

    return saveAndRetrieveCase(caze);
  }

  private Case saveAndRetrieveCase(Case caze) {

    caseRepository.save(caze);

    UacQidLink uacQidLink = new UacQidLink();
    uacQidLink.setId(UUID.randomUUID());
    uacQidLink.setActive(true);
    uacQidLink.setQid("Q123");
    uacQidLink.setUac("test_uac");
    uacQidLink.setUacHash("fakeHash");
    uacQidLink.setCaze(caze);
    uacQidLinkRepository.save(uacQidLink);

    caze =
        caseRepository
            .findById(caze.getId())
            .orElseThrow(() -> new RuntimeException("Case not found!"));
    caze.setUacQidLinks(List.of(uacQidLink));
    caseRepository.saveAndFlush(caze);

    return caseRepository
        .findById(caze.getId())
        .orElseThrow(() -> new RuntimeException("Case not found!"));
  }

  private String createUrl(String urlFormat, int port, String param1) {
    return String.format(urlFormat, port, param1);
  }

  private Case getACase(String caseId) {
    Case caze = new Case(); // easyRandom.nextObject(Case.class);
    caze.setId(UUID.fromString(caseId));
    caze.setCaseRef(1L);
    caze.setEvents(null);
    caze.setUprn(TEST_UPRN_EXISTS);
    caze.setReceiptReceived(false);
    caze.setAddressType(ADDRESS_TYPE_TEST);
    caze.setUacQidLinks(null);
    return caze;
  }

  private Case setupTestCaseWithInvalid(String caseId) {
    Case caze = getACase(caseId);
    caze.setInvalid(true);

    return saveAndRetreiveCase(caze);
  }

  private Case setupUnitTestCaseWithTreatmentCode(String caseId, String treatmentCode) {
    Case caze = getACase(caseId);
    caze.setCaseType("HH");
    caze.setTreatmentCode(treatmentCode);
    caze.setRegion("E1000");
    caze.setAddressLevel("U");

    return saveAndRetreiveCase(caze);
  }

  private Case setUpSPGUnitCaseWithTreatmentCode(String caseId, String treatmentCode) {
    Case caze = getACase(caseId);
    caze.setTreatmentCode(treatmentCode);
    caze.setRegion("E1000");
    caze.setAddressLevel("U");
    caze.setCaseType("SPG");

    return saveAndRetreiveCase(caze);
  }

  private Case saveAndRetreiveCase(Case caze) {
    caseRepository.saveAndFlush(caze);

    return caseRepository
        .findById(caze.getId())
        .orElseThrow(() -> new RuntimeException("Case not found!"));
  }
}
