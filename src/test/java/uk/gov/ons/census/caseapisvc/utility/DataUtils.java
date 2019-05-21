package uk.gov.ons.census.caseapisvc.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.json.JSONArray;
import uk.gov.ons.census.caseapisvc.model.dto.CaseContainerDTO;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.model.entity.Event;
import uk.gov.ons.census.caseapisvc.model.entity.UacQidLink;

public class DataUtils {

  private static final UUID TEST1_CASE_ID = UUID.fromString("2e083ab1-41f7-4dea-a3d9-77f48458b5ca");
  private static final Long TEST1_CASE_REFERENCE_ID = 123L;

  private static final UUID TEST2_CASE_ID = UUID.fromString("3e948f6a-00bb-466d-88a7-b0990a827b53");
  private static final Long TEST2_CASE_REFERENCE_ID = 456L;

  private static final String TEST_UPRN = "123";

  private static final String TEST_RESPONSE1_WITH_EVENTS_AS_JSON;
  private static final String TEST_RESPONSE1_WITHOUT_EVENTS_AS_JSON;
  private static final String TEST_RESPONSE2_WITH_EVENTS_AS_JSON;
  private static final String TEST_RESPONSE2_WITHOUT_EVENTS_AS_JSON;

  static {
    TEST_RESPONSE1_WITH_EVENTS_AS_JSON = loadTestFile("test_response1_with_events.json");
    TEST_RESPONSE1_WITHOUT_EVENTS_AS_JSON = loadTestFile("test_response1_without_events.json");
    TEST_RESPONSE2_WITH_EVENTS_AS_JSON = loadTestFile("test_response2_with_events.json");
    TEST_RESPONSE2_WITHOUT_EVENTS_AS_JSON = loadTestFile("test_response2_without_events.json");
  }

  public static CaseContainerDTO createSingleCaseContainerDTOWithEvents1() throws IOException {
    return createCaseContainerDTOFromJson(TEST_RESPONSE1_WITH_EVENTS_AS_JSON);
  }

  public static CaseContainerDTO createSingleCaseContainerDTOWithoutEvents1() throws IOException {
    return createCaseContainerDTOFromJson(TEST_RESPONSE1_WITHOUT_EVENTS_AS_JSON);
  }

  private static CaseContainerDTO createCaseContainerDTOWithoutEvents2() throws IOException {
    return createCaseContainerDTOFromJson(TEST_RESPONSE2_WITHOUT_EVENTS_AS_JSON);
  }

  public static List<CaseContainerDTO> createMultipleCaseContainerDTOsWithEvents()
      throws IOException {
    return Arrays.asList(
        createSingleCaseContainerDTOWithEvents1(), createSingleCaseContainerDTOWithEvents2());
  }

  public static List<CaseContainerDTO> createMultipleCaseContainerDTOWithoutEvents()
      throws IOException {
    return Arrays.asList(
        createSingleCaseContainerDTOWithoutEvents1(), createCaseContainerDTOWithoutEvents2());
  }

  public static Case createSingleCaseWithEvents() {
    return createCase(TEST1_CASE_ID, TEST1_CASE_REFERENCE_ID);
  }

  public static List<Case> createMultipleCasesWithEvents() {
    return Arrays.asList(
        createCase(TEST1_CASE_ID, TEST1_CASE_REFERENCE_ID),
        createCase(TEST2_CASE_ID, TEST2_CASE_REFERENCE_ID));
  }

  private static CaseContainerDTO createSingleCaseContainerDTOWithEvents2() throws IOException {
    return createCaseContainerDTOFromJson(TEST_RESPONSE2_WITH_EVENTS_AS_JSON);
  }

  private static CaseContainerDTO createCaseContainerDTOFromJson(String json) throws IOException {
    return new ObjectMapper().readValue(json, CaseContainerDTO.class);
  }

  private static Case createCase(UUID caseId, Long caseRef) {
    List<UacQidLink> uacQidLinks = new LinkedList<>();
    List<Event> events = new LinkedList<>();

    UacQidLink uacQidLink = new UacQidLink();
    uacQidLink.setId(UUID.randomUUID());
    uacQidLink.setUniqueNumber(new Random().nextLong());
    uacQidLink.setUac("any iac");

    Event event = new Event();
    event.setId(UUID.randomUUID());
    event.setEventDescription("Case created");
    event.setUacQidLink(uacQidLink);
    events.add(event);

    uacQidLink.setEvents(events);
    uacQidLinks.add(uacQidLink);

    Case caze = new Case();
    caze.setCaseRef(caseRef);
    caze.setCaseId(caseId);
    caze.setUprn(TEST_UPRN);
    caze.setUacQidLinks(uacQidLinks);

    return caze;
  }

  private static String loadTestFile(String filename) {
    String data = null;
    Stream<String> lines = null;

    try {
      Path path =
          Paths.get(
              Objects.requireNonNull(DataUtils.class.getClassLoader().getResource(filename))
                  .toURI());
      lines = Files.lines(path);

      data = lines.collect(Collectors.joining("\n"));
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      Objects.requireNonNull(lines).close();
    }

    return data;
  }

  public static CaseContainerDTO extractCaseContainerDTOFromResponse(
      HttpResponse<JsonNode> response) throws IOException {
    return new ObjectMapper()
        .readValue(response.getBody().getObject().toString(), CaseContainerDTO.class);
  }

  public static List<CaseContainerDTO> extractCaseContainerDTOsFromResponse(
      HttpResponse<JsonNode> response) throws IOException {
    List<CaseContainerDTO> dtos = new LinkedList<>();
    JSONArray elements = response.getBody().getArray();
    ObjectMapper mapper = new ObjectMapper();

    for (int i = 0; i < elements.length(); i++) {
      dtos.add(mapper.readValue(elements.get(i).toString(), CaseContainerDTO.class));
    }

    return dtos;
  }
}
