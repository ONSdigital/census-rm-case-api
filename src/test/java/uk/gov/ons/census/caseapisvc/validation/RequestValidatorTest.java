package uk.gov.ons.census.caseapisvc.validation;

import static org.junit.Assert.fail;

import java.util.UUID;
import org.junit.Test;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.ons.census.caseapisvc.model.entity.Case;

public class RequestValidatorTest {
  @Test
  public void testAllTheValidGetNewQidByCaseIdRequests() {
    testValidGetNewQidByCaseIdCombination("HH", "U", false, null);
    testValidGetNewQidByCaseIdCombination("HH", "U", true, "test_case_id");

    testValidGetNewQidByCaseIdCombination("CE", "E", false, null);
    testValidGetNewQidByCaseIdCombination("CE", "E", true, null);

    testValidGetNewQidByCaseIdCombination("CE", "U", true, null);

    testValidGetNewQidByCaseIdCombination("SPG", "E", false, null);
    testValidGetNewQidByCaseIdCombination("SPG", "E", true, null);

    testValidGetNewQidByCaseIdCombination("SPG", "U", false, null);
    testValidGetNewQidByCaseIdCombination("SPG", "U", true, null);
  }

  @Test
  public void testAllTheInvalidGetNewQidByCaseIdRequests() {
    testInvalidGetNewQidByCaseIdCombination("HH", "U", true, null);
    testInvalidGetNewQidByCaseIdCombination("HH", "U", false, "test_case_id");
    testInvalidGetNewQidByCaseIdCombination("CE", "E", false, "test_case_id");
    testInvalidGetNewQidByCaseIdCombination("CE", "E", true, "test_case_id");
    testInvalidGetNewQidByCaseIdCombination("CE", "U", false, null);
    testInvalidGetNewQidByCaseIdCombination("CE", "U", false, "test_case_id");
    testInvalidGetNewQidByCaseIdCombination("CE", "U", true, "test_case_id");
    testInvalidGetNewQidByCaseIdCombination("SPG", "E", false, "test_case_id");
    testInvalidGetNewQidByCaseIdCombination("SPG", "E", true, "test_case_id");
    testInvalidGetNewQidByCaseIdCombination("SPG", "U", false, "test_case_id");
    testInvalidGetNewQidByCaseIdCombination("SPG", "U", true, "test_case_id");
  }

  private void testValidGetNewQidByCaseIdCombination(
      String caseType, String addressLevel, boolean individual, String individualCaseId) {
    UUID caseId = UUID.randomUUID();
    Case caze = new Case();
    caze.setCaseId(caseId);
    caze.setCaseType(caseType);
    caze.setAddressLevel(addressLevel);

    RequestValidator.validateGetNewQidByCaseIdRequest(caze, individual, individualCaseId);
  }

  private void testInvalidGetNewQidByCaseIdCombination(
      String caseType, String addressLevel, boolean individual, String individualCaseId) {
    UUID caseId = UUID.randomUUID();
    Case caze = new Case();
    caze.setCaseId(caseId);
    caze.setCaseType(caseType);
    caze.setAddressLevel(addressLevel);

    try {
      RequestValidator.validateGetNewQidByCaseIdRequest(caze, individual, individualCaseId);
    } catch (ResponseStatusException responseStatusException) {
      // It worked
      return;
    }

    fail("Invalid request was not detected");
  }
}
