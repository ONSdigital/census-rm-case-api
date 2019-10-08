package uk.gov.ons.census.caseapisvc.exception;

import java.util.UUID;

public class QidNotFoundException extends RuntimeException {
  public QidNotFoundException(String qid) {
    super(String.format("Qid Not Found '%s' not found", qid));
  }
  public QidNotFoundException(UUID caseId) {
    super(String.format("Qid Not Found for case ID: '%s'", caseId));
  }
}
