package uk.gov.ons.census.caseapisvc.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.ons.census.caseapisvc.model.dto.EmptyResponseDTO;

@SuppressWarnings("unchecked")
@ControllerAdvice
public class ApplicationControllerAdvice {

  @ExceptionHandler(HttpClientErrorException.class)
  @ResponseBody
  public ResponseEntity handleHttpClientErrorException(HttpClientErrorException hnfe) {
    HttpStatus statusCode = hnfe.getStatusCode();

    switch (statusCode) {
      case BAD_REQUEST:
        return buildResponseEntity(
            statusCode,
            "Bad request. Indicates an issue with the request. Further details are provided in the response.");
      case UNAUTHORIZED:
        return buildResponseEntity(
            statusCode, "Unauthorised. The API key provided with the request is invalid.");
      case NOT_FOUND:
        return new ResponseEntity(new EmptyResponseDTO(), HttpStatus.NOT_FOUND);
      case TOO_MANY_REQUESTS:
        return buildResponseEntity(
            statusCode, "Server too busy. The ONS API is experiencing exceptional load.");
      default:
        return buildResponseEntity(statusCode, "Unexpected Internal Server Error");
    }
  }

  @ResponseStatus(INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public String handleUnexpectedException(final Exception e) throws Exception {
    // TODO log etc... here
    throw e;
  }

  private ResponseEntity<Object> buildResponseEntity(HttpStatus statusCode, String message) {
    return new ResponseEntity(new ErrorResponse(statusCode.toString(), message), statusCode);
  }

  @Data
  public static class ErrorResponse {
    private final String code;
    private final String message;
  }
}
