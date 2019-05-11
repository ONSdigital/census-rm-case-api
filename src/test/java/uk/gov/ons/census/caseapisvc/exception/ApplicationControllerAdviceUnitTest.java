package uk.gov.ons.census.caseapisvc.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.PAYMENT_REQUIRED;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

public class ApplicationControllerAdviceUnitTest {

  private String EXPECTED_BAD_REQUEST_MESSAGE =
      "Bad request. Indicates an issue with the request. Further details are provided in the response.";
  private String EXPECTED_UNAUTHORISED_MESSAGE =
      "Unauthorised. The API key provided with the request is invalid.";
  private String EXPECTED_TOO_MANY_REQUESTS_MESSAGE =
      "Server too busy. The ONS API is experiencing exceptional load.";
  private String EXPECTED_UNEXPECTED_EXCEPTION_MESSAGE = "Unexpected Internal Server Error";

  ApplicationControllerAdvice applicationControllerAdvice;

  @Before
  public void setUp() {
    this.applicationControllerAdvice = new ApplicationControllerAdvice();
  }

  @Test
  public void handleBadRequestException() {
    ResponseEntity response =
        applicationControllerAdvice.handleHttpClientErrorException(
            new HttpClientErrorException(BAD_REQUEST, "anything"));

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    assertThat(response.getBody().toString()).contains(EXPECTED_BAD_REQUEST_MESSAGE);
  }

  @Test
  public void handleUnauthorizedException() {
    ResponseEntity response =
        applicationControllerAdvice.handleHttpClientErrorException(
            new HttpClientErrorException(UNAUTHORIZED, "anything"));

    assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    assertThat(response.getBody().toString()).contains(EXPECTED_UNAUTHORISED_MESSAGE);
  }

  @Test
  public void handleNotFoundException() {
    ResponseEntity response =
        applicationControllerAdvice.handleHttpClientErrorException(
            new HttpClientErrorException(NOT_FOUND, "anything"));

    assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  public void handleTooManyRequestsException() {
    ResponseEntity response =
        applicationControllerAdvice.handleHttpClientErrorException(
            new HttpClientErrorException(TOO_MANY_REQUESTS, "anything"));

    assertThat(response.getStatusCode()).isEqualTo(TOO_MANY_REQUESTS);
    assertThat(response.getBody().toString()).contains(EXPECTED_TOO_MANY_REQUESTS_MESSAGE);
  }

  @Test
  public void handleUnexpectedException() {
    ResponseEntity response =
        applicationControllerAdvice.handleHttpClientErrorException(
            new HttpClientErrorException(PAYMENT_REQUIRED, "anything"));

    assertThat(response.getStatusCode()).isEqualTo(PAYMENT_REQUIRED);
    assertThat(response.getBody().toString()).contains(EXPECTED_UNEXPECTED_EXCEPTION_MESSAGE);
  }
}
