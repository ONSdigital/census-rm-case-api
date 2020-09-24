package uk.gov.ons.census.caseapisvc.validation;

import static org.junit.Assert.fail;

import java.util.UUID;
import org.junit.Test;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.ons.census.caseapisvc.model.entity.Case;

public class RequestValidatorTest {
  @Test
  public void testAllTheValidGetNewQidByCaseIdRequests() {
    testValidGetNewQidByCaseIdCombination("HH", "U", "CENSUS", false, null);
    testValidGetNewQidByCaseIdCombination("HH", "U", "CENSUS", true, UUID.randomUUID());

    testValidGetNewQidByCaseIdCombination("CE", "E", "CENSUS", false, null);
    testValidGetNewQidByCaseIdCombination("CE", "E", "CENSUS", true, null);

    testValidGetNewQidByCaseIdCombination("CE", "U", "CENSUS", true, null);

    testValidGetNewQidByCaseIdCombination("SPG", "E", "CENSUS", false, null);
    testValidGetNewQidByCaseIdCombination("SPG", "E", "CENSUS", true, null);

    testValidGetNewQidByCaseIdCombination("SPG", "U", "CENSUS", false, null);
    testValidGetNewQidByCaseIdCombination("SPG", "U", "CENSUS", true, null);

    testValidGetNewQidByCaseIdCombination("HH", "U", "CCS", false, null);
    testValidGetNewQidByCaseIdCombination("SPG", "E", "CCS", false, null);
    testValidGetNewQidByCaseIdCombination("SPG", "U", "CCS", false, null);
  }

  @Test
  public void testAllTheInvalidGetNewQidByCaseIdRequests() {
    testInvalidGetNewQidByCaseIdCombination("HH", "U", true, null);
    testInvalidGetNewQidByCaseIdCombination("HH", "U", false, UUID.randomUUID());
    testInvalidGetNewQidByCaseIdCombination("CE", "E", false, UUID.randomUUID());
    testInvalidGetNewQidByCaseIdCombination("CE", "E", true, UUID.randomUUID());
    testInvalidGetNewQidByCaseIdCombination("CE", "U", false, null);
    testInvalidGetNewQidByCaseIdCombination("CE", "U", false, UUID.randomUUID());
    testInvalidGetNewQidByCaseIdCombination("CE", "U", true, UUID.randomUUID());
    testInvalidGetNewQidByCaseIdCombination("SPG", "E", false, UUID.randomUUID());
    testInvalidGetNewQidByCaseIdCombination("SPG", "E", true, UUID.randomUUID());
    testInvalidGetNewQidByCaseIdCombination("SPG", "U", false, UUID.randomUUID());
    testInvalidGetNewQidByCaseIdCombination("SPG", "U", true, UUID.randomUUID());
  }

  @Test(expected = ResponseStatusException.class)
  public void testCCSAndIndividualFails() {
    Case caze = new Case();
    caze.setCaseId(UUID.randomUUID());
    caze.setSurvey("CCS");

    RequestValidator.validateGetNewQidByCaseIdRequest(caze, true, null);
  }

  @Test(expected = ResponseStatusException.class)
  public void testCCSAndCEFails() {
    Case caze = new Case();
    caze.setCaseId(UUID.randomUUID());
    caze.setSurvey("CCS");
    caze.setCaseType("CE");

    RequestValidator.validateGetNewQidByCaseIdRequest(caze, false, null);
  }

  private void testValidGetNewQidByCaseIdCombination(
      String caseType,
      String addressLevel,
      String surveyType,
      boolean individual,
      UUID individualCaseId) {
    UUID caseId = UUID.randomUUID();
    Case caze = new Case();
    caze.setCaseId(caseId);
    caze.setCaseType(caseType);
    caze.setAddressLevel(addressLevel);
    caze.setSurvey(surveyType);

    RequestValidator.validateGetNewQidByCaseIdRequest(caze, individual, individualCaseId);
  }

  private void testInvalidGetNewQidByCaseIdCombination(
      String caseType, String addressLevel, boolean individual, UUID individualCaseId) {
    testInvalidGetNewQidByCaseIdCombination(
        caseType, addressLevel, "CENSUS", individual, individualCaseId);
  }

  private void testInvalidGetNewQidByCaseIdCombination(
      String caseType,
      String addressLevel,
      String surveyType,
      boolean individual,
      UUID individualCaseId) {
    UUID caseId = UUID.randomUUID();
    Case caze = new Case();
    caze.setCaseId(caseId);
    caze.setCaseType(caseType);
    caze.setAddressLevel(addressLevel);
    caze.setSurvey(surveyType);

    try {
      RequestValidator.validateGetNewQidByCaseIdRequest(caze, individual, individualCaseId);

    } catch (ResponseStatusException responseStatusException) {
      // It worked
      return;
    }

    fail("Invalid request was not detected");
  }
}
