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
    testValidGetNewQidByCaseIdCombination("HH", "U", true, UUID.randomUUID());

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

  private void testValidGetNewQidByCaseIdCombination(
      String caseType, String addressLevel, boolean individual, UUID individualCaseId) {
    UUID caseId = UUID.randomUUID();
    Case caze = new Case();
    caze.setCaseId(caseId);
    caze.setCaseType(caseType);
    caze.setAddressLevel(addressLevel);

    RequestValidator.validateGetNewQidByCaseIdRequest(caze, individual, individualCaseId);
  }

  private void testInvalidGetNewQidByCaseIdCombination(
      String caseType, String addressLevel, boolean individual, UUID individualCaseId) {
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
