package uk.gov.ons.census.caseapisvc.exception;

public class UacQidLinkWithNoCaseException extends RuntimeException {
  public UacQidLinkWithNoCaseException(String qid) {
    super(String.format("Qid '%s' link but has not case not found", qid));
  }
}
