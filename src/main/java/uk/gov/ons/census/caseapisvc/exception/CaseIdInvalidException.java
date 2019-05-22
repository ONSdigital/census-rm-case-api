package uk.gov.ons.census.caseapisvc.exception;

public class CaseIdInvalidException extends RuntimeException {
  public CaseIdInvalidException(String caseId) {
    super(String.format("Case Id '%s' is invalid", caseId));
  }
}
