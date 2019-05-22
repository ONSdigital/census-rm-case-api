package uk.gov.ons.census.caseapisvc.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createMultipleCaseContainerDTOWithoutEvents;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createMultipleCaseContainerDTOsWithEvents;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createSingleCaseContainerDTOWithEvents1;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createSingleCaseContainerDTOWithoutEvents1;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.extractCaseContainerDTOFromResponse;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.extractCaseContainerDTOsFromResponse;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.census.caseapisvc.model.dto.CaseContainerDTO;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql("/data-test.sql")
public class CaseEndpointIT {

  private static final String TEST_UPRN_EXISTS = "123456789012345";
  private static final String TEST_UPRN_DOES_NOT_EXIST = "999999999999999";

  private static final String TEST_CASE_ID_EXISTS = "c0d4f87d-9d19-4393-80c9-9eb94f69c460";
  private static final String TEST_CASE_ID_DOES_NOT_EXIST = "590179eb-f8ce-4e2d-8cb6-ca4013a2ccf0";
  private static final String TEST_INVALID_CASE_ID = "anything";

  private static final String TEST_REFERENCE_ID_EXISTS = "10000000";
  private static final String TEST_REFERENCE_DOES_NOT_EXIST = "99999999";

  @LocalServerPort private int port;

  @Test
  public void shouldRetrieveMultipleCasesWithEventsWhenSearchingByUPRN() throws Exception {
    List<CaseContainerDTO> expectedData = createMultipleCaseContainerDTOsWithEvents();

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/uprn/%s", port, TEST_UPRN_EXISTS))
            .header("accept", "application/json")
            .queryString("caseEvents", "true")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    List<CaseContainerDTO> actualData = extractCaseContainerDTOsFromResponse(response);

    assertThat(actualData).containsExactlyInAnyOrder(expectedData.get(0), expectedData.get(1));
  }

  @Test
  public void shouldRetrieveMultipleCasesWithoutEventsWhenSearchingByUPRN() throws Exception {
    List<CaseContainerDTO> expectedData = createMultipleCaseContainerDTOWithoutEvents();

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/uprn/%s", port, TEST_UPRN_EXISTS))
            .header("accept", "application/json")
            .queryString("caseEvents", "false")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    List<CaseContainerDTO> actualData = extractCaseContainerDTOsFromResponse(response);

    assertThat(actualData).containsExactlyInAnyOrder(expectedData.get(0), expectedData.get(1));
  }

  @Test
  public void shouldRetrieveMultipleCasesWithoutEventsByDefaultWhenSearchingByUPRN()
      throws Exception {
    List<CaseContainerDTO> expectedData = createMultipleCaseContainerDTOWithoutEvents();

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/uprn/%s", port, TEST_UPRN_EXISTS))
            .header("accept", "application/json")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    List<CaseContainerDTO> actualData = extractCaseContainerDTOsFromResponse(response);

    assertThat(actualData).containsExactlyInAnyOrder(expectedData.get(0), expectedData.get(1));
  }

  @Test
  public void shouldReturn404WhenUPRNNotFound() throws Exception {
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/uprn/%s", port, TEST_UPRN_DOES_NOT_EXIST))
            .header("accept", "application/json")
            .asJson();

    assertThat(jsonResponse.getStatus()).isEqualTo(NOT_FOUND.value());
  }

  @Test
  public void shouldRetrieveACaseWithEventsWhenSearchingByCaseId() throws Exception {
    CaseContainerDTO expectedData = createSingleCaseContainerDTOWithEvents1();

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/%s", port, TEST_CASE_ID_EXISTS))
            .header("accept", "application/json")
            .queryString("caseEvents", "true")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    CaseContainerDTO actualData = extractCaseContainerDTOFromResponse(response);

    assertThat(actualData).isEqualTo(expectedData);
  }

  @Test
  public void shouldRetrieveACaseWithoutEventsWhenSearchingByCaseId() throws Exception {
    CaseContainerDTO expectedData = createSingleCaseContainerDTOWithoutEvents1();

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/%s", port, TEST_CASE_ID_EXISTS))
            .header("accept", "application/json")
            .queryString("caseEvents", "false")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    CaseContainerDTO actualData = extractCaseContainerDTOFromResponse(response);

    assertThat(actualData).isEqualTo(expectedData);
  }

  @Test
  public void shouldRetrieveACaseWithoutEventsByDefaultWhenSearchingByCaseId() throws Exception {
    CaseContainerDTO expectedData = createSingleCaseContainerDTOWithoutEvents1();

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/%s", port, TEST_CASE_ID_EXISTS))
            .header("accept", "application/json")
            .queryString("caseEvents", "false")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    CaseContainerDTO actualData = extractCaseContainerDTOFromResponse(response);

    assertThat(actualData).isEqualTo(expectedData);
  }

  @Test
  public void shouldReturn404WhenCaseIdNotFound() throws UnirestException {
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/%s", port, TEST_CASE_ID_DOES_NOT_EXIST))
            .header("accept", "application/json")
            .asJson();

    assertThat(jsonResponse.getStatus()).isEqualTo(NOT_FOUND.value());
  }

  @Test
  public void shouldReturn404WhenInvalidCaseId() throws UnirestException {
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(createUrl("http://localhost:%d/cases/%s", port, TEST_INVALID_CASE_ID))
            .header("accept", "application/json")
            .asJson();

    assertThat(jsonResponse.getStatus()).isEqualTo(NOT_FOUND.value());
  }

  @Test
  public void shouldRetrieveACaseWithEventsWhenSearchingByCaseReference() throws Exception {
    CaseContainerDTO expectedData = createSingleCaseContainerDTOWithEvents1();

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/ref/%s", port, TEST_REFERENCE_ID_EXISTS))
            .header("accept", "application/json")
            .queryString("caseEvents", "true")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    CaseContainerDTO actualData = extractCaseContainerDTOFromResponse(response);

    assertThat(actualData).isEqualTo(expectedData);
  }

  @Test
  public void shouldRetrieveACaseWithoutEventsWhenSearchingByCaseReference() throws Exception {
    CaseContainerDTO expectedData = createSingleCaseContainerDTOWithoutEvents1();

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/ref/%s", port, TEST_REFERENCE_ID_EXISTS))
            .header("accept", "application/json")
            .queryString("caseEvents", "false")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    CaseContainerDTO actualData = extractCaseContainerDTOFromResponse(response);

    assertThat(actualData).isEqualTo(expectedData);
  }

  @Test
  public void shouldRetrieveACaseWithoutEventsByDefaultWhenSearchingByCaseReference()
      throws Exception {
    CaseContainerDTO expectedData = createSingleCaseContainerDTOWithoutEvents1();

    HttpResponse<JsonNode> response =
        Unirest.get(createUrl("http://localhost:%d/cases/ref/%s", port, TEST_REFERENCE_ID_EXISTS))
            .header("accept", "application/json")
            .queryString("caseEvents", "false")
            .asJson();

    assertThat(response.getStatus()).isEqualTo(OK.value());

    CaseContainerDTO actualData = extractCaseContainerDTOFromResponse(response);

    assertThat(actualData).isEqualTo(expectedData);
  }

  @Test
  public void shouldReturn404WhenCaseReferenceNotFound() throws Exception {
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(
                createUrl("http://localhost:%d/cases/ref/%s", port, TEST_REFERENCE_DOES_NOT_EXIST))
            .header("accept", "application/json")
            .asJson();

    assertThat(jsonResponse.getStatus()).isEqualTo(NOT_FOUND.value());
  }

  private String createUrl(String urlFormat, int port, String param1) {
    return String.format(urlFormat, port, param1);
  }
}
