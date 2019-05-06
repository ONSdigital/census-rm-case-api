package uk.gov.ons.census.casesvc.exception;

public class CaseNotFoundException extends RuntimeException {
  public CaseNotFoundException(String message) {
    super(message);
  }
}
