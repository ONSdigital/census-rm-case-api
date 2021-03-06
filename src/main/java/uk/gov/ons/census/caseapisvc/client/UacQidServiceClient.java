package uk.gov.ons.census.caseapisvc.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedPayloadDTO;

@Component
public class UacQidServiceClient {

  @Value("${uacservice.connection.scheme}")
  private String scheme;

  @Value("${uacservice.connection.host}")
  private String host;

  @Value("${uacservice.connection.port}")
  private String port;

  public UacQidCreatedPayloadDTO generateUacQid(int questionnaireType) {

    RestTemplate restTemplate = new RestTemplate();
    UriComponents uriComponents = createUriComponents(questionnaireType);
    ResponseEntity<UacQidCreatedPayloadDTO> responseEntity =
        restTemplate.exchange(
            uriComponents.toUri(), HttpMethod.GET, null, UacQidCreatedPayloadDTO.class);
    return responseEntity.getBody();
  }

  private UriComponents createUriComponents(int questionnaireType) {
    return UriComponentsBuilder.newInstance()
        .scheme(scheme)
        .host(host)
        .port(port)
        .queryParam("questionnaireType", questionnaireType)
        .build()
        .encode();
  }
}
