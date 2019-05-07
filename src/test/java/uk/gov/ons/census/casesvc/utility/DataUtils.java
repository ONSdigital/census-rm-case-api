package uk.gov.ons.census.casesvc.utility;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import uk.gov.ons.census.casesvc.model.entity.Case;
import uk.gov.ons.census.casesvc.model.entity.CaseState;
import uk.gov.ons.census.casesvc.model.entity.Event;
import uk.gov.ons.census.casesvc.model.entity.UacQidLink;

public class DataUtils {

  private static UUID TEST1_CASE_ID = UUID.fromString("2e083ab1-41f7-4dea-a3d9-77f48458b5ca");
  private static Long TEST1_CASE_REFERENCE_ID = 123L;

  private static UUID TEST2_CASE_ID = UUID.fromString("3e948f6a-00bb-466d-88a7-b0990a827b53");
  private static Long TEST2_CASE_REFERENCE_ID = 456L;

  private static UUID TEST3_CASE_ID = UUID.fromString("4ee74fac-e0fd-41ac-8322-414b8d5e978d");
  private static Long TEST3_CASE_REFERENCE_ID = 789L;

  private static String TEST_UPRN = "123";

  public static Case create1TestCase() {
    return createTestCase(TEST1_CASE_ID, TEST_UPRN, TEST1_CASE_REFERENCE_ID);
  }

  public static List<Case> create3TestCases() {
    return Arrays.asList(
        createTestCase(TEST1_CASE_ID, TEST_UPRN, TEST1_CASE_REFERENCE_ID),
        createTestCase(TEST2_CASE_ID, TEST_UPRN, TEST2_CASE_REFERENCE_ID),
        createTestCase(TEST3_CASE_ID, TEST_UPRN, TEST3_CASE_REFERENCE_ID));
  }

  private static Case createTestCase(UUID caseId, String uprn, Long caseRef) {
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

    return Case.builder()
        .caseRef(caseRef)
        .caseId(caseId)
        .arid("arid")
        .estabArid("estabArid")
        .uprn(uprn)
        .addressType("addressType")
        .estabType("estabType")
        .addressLevel("addressLevel")
        .abpCode("abpCode")
        .organisationName("organisationName")
        .addressLine1("addressLine1")
        .addressLine2("addressLine2")
        .addressLine3("addressLine3")
        .townName("townName")
        .postcode("postcode")
        .latitude("latitude")
        .longitude("longitude")
        .oa("oa")
        .lsoa("lsoa")
        .msoa("msoa")
        .lad("lad")
        .rgn("rgn")
        .htcWillingness("htcWillingness")
        .htcDigital("htcDigital")
        .treatmentCode("treatmentCode")
        .collectionExerciseId("collectionExerciseId")
        .actionPlanId("actionPlanId")
        .state(CaseState.ACTIONABLE)
        .uacQidLinks(uacQidLinks)
        .build();
  }
}
