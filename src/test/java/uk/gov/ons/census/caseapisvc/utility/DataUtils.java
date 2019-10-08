package uk.gov.ons.census.caseapisvc.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import uk.gov.ons.census.caseapisvc.model.dto.CaseContainerDTO;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.model.entity.Event;
import uk.gov.ons.census.caseapisvc.model.entity.UacQidLink;

public class DataUtils {

  private static final UUID TEST1_CASE_ID = UUID.fromString("2e083ab1-41f7-4dea-a3d9-77f48458b5ca");
  private static final int TEST1_CASE_REFERENCE_ID = 123;

  private static final UUID TEST2_CASE_ID = UUID.fromString("3e948f6a-00bb-466d-88a7-b0990a827b53");
  private static final int TEST2_CASE_REFERENCE_ID = 456;

  private static final String TEST_UPRN = "123";

  public static final String TEST_CCS_QID = "7120000000000000";

  public static final ObjectMapper mapper;

  static {
    mapper = new ObjectMapper().registerModule(new JavaTimeModule());
  }

  public static Case createSingleCaseWithEvents() {
    return createCase(TEST1_CASE_ID, TEST1_CASE_REFERENCE_ID);
  }

  public static Case createSingleCcsCaseWithCcsQid() {
    return createCcsCase(TEST1_CASE_ID, TEST1_CASE_REFERENCE_ID, TEST_CCS_QID);
  }

  public static List<Case> createMultipleCasesWithEvents() {
    return Arrays.asList(
        createCase(TEST1_CASE_ID, TEST1_CASE_REFERENCE_ID),
        createCase(TEST2_CASE_ID, TEST2_CASE_REFERENCE_ID));
  }

  private static Case createCase(UUID caseId, int caseRef) {
    List<UacQidLink> uacQidLinks = new LinkedList<>();
    List<Event> events = new LinkedList<>();

    UacQidLink uacQidLink = new UacQidLink();
    uacQidLink.setId(UUID.randomUUID());
    uacQidLink.setUac("any UAC");

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

  public static Case createCcsCase(UUID caseId, int caseRef, String qid) {
    List<UacQidLink> uacQidLinks = new LinkedList<>();

    UacQidLink uacQidLink = createCcsUacQidLink(qid, true);
    uacQidLinks.add(uacQidLink);

    Case caze = new Case();
    caze.setCaseRef(caseRef);
    caze.setCaseId(caseId);
    caze.setUacQidLinks(uacQidLinks);

    return caze;
  }

  public static UacQidLink createCcsUacQidLink(String qid, boolean active) {
    UacQidLink ccsUacQidLink = new UacQidLink();
    ccsUacQidLink.setId(UUID.randomUUID());
    ccsUacQidLink.setUac("any UAC");
    ccsUacQidLink.setQid(qid);
    ccsUacQidLink.setCcsCase(true);
    ccsUacQidLink.setActive(active);
    return ccsUacQidLink;
  }

  public static CaseContainerDTO extractCaseContainerDTOFromResponse(
      HttpResponse<JsonNode> response) throws IOException {
    return mapper.readValue(response.getBody().getObject().toString(), CaseContainerDTO.class);
  }

  public static CaseContainerDTO extractCaseIdDtoFromResponse(HttpResponse<JsonNode> response)
      throws IOException {
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
}
