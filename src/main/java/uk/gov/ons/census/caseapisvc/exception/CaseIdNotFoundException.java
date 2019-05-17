package uk.gov.ons.census.caseapisvc.exception;

public class CaseIdNotFoundException extends RuntimeException {
  public CaseIdNotFoundException(String caseId) {
    super(String.format("Case Id '%s' not found", caseId));
  }
}
