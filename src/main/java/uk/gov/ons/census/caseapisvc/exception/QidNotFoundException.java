package uk.gov.ons.census.caseapisvc.exception;

public class QidNotFoundException extends RuntimeException {
  public QidNotFoundException(String qid) { super(String.format("Qid Not Found '%s' not found", qid)); }
}
