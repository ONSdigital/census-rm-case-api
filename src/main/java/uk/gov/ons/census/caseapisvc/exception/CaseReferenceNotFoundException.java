package uk.gov.ons.census.caseapisvc.exception;

public class CaseReferenceNotFoundException extends RuntimeException {
  public CaseReferenceNotFoundException(Long caseId) {
    super(String.format("Case Reference '%s' not found", caseId));
  }
}
