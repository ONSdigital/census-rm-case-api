package uk.gov.ons.census.caseapisvc.exception;

public class CCSQidNotFoundException extends RuntimeException {
  public CCSQidNotFoundException(String caseId) {
    super(String.format("CCS Qid not found for case ID %s", caseId));
  }
}
