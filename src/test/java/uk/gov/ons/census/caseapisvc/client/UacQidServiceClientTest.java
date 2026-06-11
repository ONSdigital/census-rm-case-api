package uk.gov.ons.census.caseapisvc.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedPayloadDTO;

class UacQidServiceClientTest {

  @Test
  void generateUacQid_callsRestTemplateWithCorrectUri() {
    // Arrange
    UacQidServiceClient client = new UacQidServiceClient();

    // Inject @Value fields manually
    setField(client, "scheme", "http");
    setField(client, "host", "localhost");
    setField(client, "port", "8080");

    UacQidCreatedPayloadDTO payload = new UacQidCreatedPayloadDTO();
    payload.setCaseId(null); // any value is fine

    ResponseEntity<UacQidCreatedPayloadDTO> responseEntity = ResponseEntity.ok(payload);

    try (MockedConstruction<RestTemplate> mocked =
        mockConstruction(
            RestTemplate.class,
            (mock, context) ->
                when(mock.exchange(
                        any(), eq(HttpMethod.GET), eq(null), eq(UacQidCreatedPayloadDTO.class)))
                    .thenReturn(responseEntity))) {

      // Act
      UacQidCreatedPayloadDTO result = client.generateUacQid(1);

      // Assert
      assertSame(payload, result);

      RestTemplate restTemplate = mocked.constructed().get(0);

      verify(restTemplate)
          .exchange(
              argThat(
                  uri -> {
                    String uriString = uri.toString();
                    return uriString.equals("http://localhost:8080");
                  }),
              eq(HttpMethod.GET),
              eq(null),
              eq(UacQidCreatedPayloadDTO.class));
    }
  }

  // Helper to set private fields
  private static void setField(Object target, String fieldName, Object value) {
    try {
      var field = UacQidServiceClient.class.getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
