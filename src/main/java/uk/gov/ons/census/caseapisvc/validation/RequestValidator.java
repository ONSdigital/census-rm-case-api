package uk.gov.ons.census.caseapisvc.validation;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.ons.census.caseapisvc.model.entity.Case;

public class RequestValidator {
  public static void validateGetNewQidByCaseIdRequest(
      Case caze, boolean individual, UUID individualCaseId) {
    if (caze.getCaseType().equals("HH") && individual && individualCaseId == null) {
      throwBadRequest();
    } else if (caze.getCaseType().equals("HH") && !individual && individualCaseId != null) {
      throwBadRequest();
    } else if (caze.getCaseType().equals("CE")
        && caze.getAddressLevel().equals("E")
        && !individual
        && individualCaseId != null) {
      throwBadRequest();
    } else if (caze.getCaseType().equals("CE")
        && caze.getAddressLevel().equals("E")
        && individual
        && individualCaseId != null) {
      throwBadRequest();
    } else if (caze.getCaseType().equals("CE")
        && caze.getAddressLevel().equals("U")
        && individual
        && individualCaseId == null) {
      return; // Valid request
    } else if (caze.getCaseType().equals("CE") && caze.getAddressLevel().equals("U")) {
      throwBadRequest();
    } else if (caze.getCaseType().equals("SPG")
        && caze.getAddressLevel().equals("E")
        && !individual
        && individualCaseId != null) {
      throwBadRequest();
    } else if (caze.getCaseType().equals("SPG")
        && caze.getAddressLevel().equals("E")
        && individual
        && individualCaseId != null) {
      throwBadRequest();
    } else if (caze.getCaseType().equals("SPG")
        && caze.getAddressLevel().equals("U")
        && !individual
        && individualCaseId != null) {
      throwBadRequest();
    } else if (caze.getCaseType().equals("SPG")
        && caze.getAddressLevel().equals("U")
        && individual
        && individualCaseId != null) {
      throwBadRequest();
    }
  }

  private static void throwBadRequest() {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
  }
}
