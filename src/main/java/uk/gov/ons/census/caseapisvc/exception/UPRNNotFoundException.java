package uk.gov.ons.census.caseapisvc.exception;

public class UPRNNotFoundException extends RuntimeException {
  public UPRNNotFoundException(String uprn) {
    super(String.format("UPRN '%s' not found", uprn));
  }
}
