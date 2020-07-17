package uk.gov.ons.census.caseapisvc.exception;

import java.util.UUID;

public class CaseIdNotFoundException extends RuntimeException {
  public CaseIdNotFoundException(UUID caseId) {
    super(String.format("Case Id '%s' not found", caseId.toString()));
  }
}
