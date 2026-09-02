package uk.gov.ons.census.caseapisvc.testutils;

import static java.time.OffsetDateTime.now;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.json.JSONArray;
import tools.jackson.databind.ObjectMapper;
import uk.gov.ons.census.caseapisvc.model.dto.CaseContainerDTO;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedPayloadDTO;
import uk.gov.ons.census.caseapisvc.utility.ObjectMapperFactory;
import uk.gov.ons.census.common.model.entity.Case;
import uk.gov.ons.census.common.model.entity.Event;
import uk.gov.ons.census.common.model.entity.EventType;
import uk.gov.ons.census.common.model.entity.UacQidLink;

public class DataUtils {

  private static final UUID TEST1_CASE_ID = UUID.fromString("2e083ab1-41f7-4dea-a3d9-77f48458b5ca");
  private static final long TEST1_CASE_REFERENCE_ID = 1234567890;

  private static final UUID TEST2_CASE_ID = UUID.fromString("3e948f6a-00bb-466d-88a7-b0990a827b53");
  private static final long TEST2_CASE_REFERENCE_ID = 1234567890;

  private static final String TEST_UPRN = "123";
  private static final String TEST_ESTAB_UPRN = "4567";

  public static final String CREATED_UAC = "created UAC";
  public static final String TEST_POSTCODE = "AB1 2BC";

  public static final ObjectMapper mapper = ObjectMapperFactory.objectMapper();

  public static Case createSingleCaseWithEvents() {
    return createCase(TEST1_CASE_ID, TEST1_CASE_REFERENCE_ID);
  }

  public static Stream<Case> createMultipleCasesWithEvents() {
    return Stream.of(
        createCase(TEST1_CASE_ID, TEST1_CASE_REFERENCE_ID),
        createCase(TEST2_CASE_ID, TEST2_CASE_REFERENCE_ID));
  }

  private static Case createCase(UUID id, long caseRef) {
    List<UacQidLink> uacQidLinks = new LinkedList<>();
    List<Event> events = new LinkedList<>();

    UacQidLink uacQidLink = createUacQidLink();

    Event event = new Event();
    event.setId(UUID.randomUUID());
    event.setDescription("Case created");
    event.setUacQidLink(uacQidLink);
    event.setType(EventType.NEW_CASE);
    events.add(event);

    uacQidLinks.add(uacQidLink);

    Case caze = new Case();
    caze.setId(id);
    caze.setCaseRef(caseRef);
    caze.setUacQidLinks(uacQidLinks);
    caze.setEvents(events);

    caze.setUprn(TEST_UPRN);
    caze.setEstabUprn(TEST_ESTAB_UPRN);
    caze.setPostcode(TEST_POSTCODE);
    caze.setCreatedAt(now());
    caze.setLastUpdatedAt(now());

    return caze;
  }

  public static UacQidCreatedPayloadDTO createUacQidCreatedPayload(String qid) {
    UacQidCreatedPayloadDTO uacQidCreatedPayloadDTO = new UacQidCreatedPayloadDTO();
    uacQidCreatedPayloadDTO.setQid(qid);
    uacQidCreatedPayloadDTO.setUac(CREATED_UAC);
    return uacQidCreatedPayloadDTO;
  }

  public static CaseContainerDTO extractCaseContainerDTOFromResponse(
      HttpResponse<JsonNode> response) throws IOException {
    return mapper.readValue(response.getBody().getObject().toString(), CaseContainerDTO.class);
  }

  public static List<CaseContainerDTO> extractCaseContainerDTOsFromResponse(
      HttpResponse<JsonNode> response) throws IOException {
    List<CaseContainerDTO> dtos = new LinkedList<>();
    JSONArray elements = response.getBody().getArray();

    for (int i = 0; i < elements.length(); i++) {
      dtos.add(mapper.readValue(elements.get(i).toString(), CaseContainerDTO.class));
    }

    return dtos;
  }

  public static UacQidLink createUacQidLink() {
    UacQidLink uacQidLink = new UacQidLink();
    uacQidLink.setId(UUID.randomUUID());
    uacQidLink.setUac("any UAC");
    uacQidLink.setQid("any QID");
    uacQidLink.setEvents(Collections.emptyList());
    return uacQidLink;
  }

  public static String createUrl(String urlFormat, String param1) {
    return String.format(urlFormat, param1);
  }
}
