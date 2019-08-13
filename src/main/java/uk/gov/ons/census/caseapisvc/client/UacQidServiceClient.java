package uk.gov.ons.census.caseapisvc.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.ons.census.caseapisvc.model.dto.CaseDetailsDTO;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidDTO;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.service.CaseService;
import uk.gov.ons.census.caseapisvc.utilities.QuestionnaireTypeHelper;

@Component
public class UacQidServiceClient {

  @Value("${uacservice.connection.scheme}")
  private String scheme;

  @Value("${uacservice.connection.host}")
  private String host;

  @Value("${uacservice.connection.port}")
  private String port;

  private final CaseService caseService;

  public UacQidServiceClient(CaseService caseService) {
    this.caseService = caseService;
  }

  public UacQidDTO generateUacQid(CaseDetailsDTO caseDetails) {
    int questionnaireType;

    if (!StringUtils.isEmpty(caseDetails.getCaseId())) {
      Case caze = caseService.findByCaseId(caseDetails.getCaseId());
      questionnaireType =
          QuestionnaireTypeHelper.calculateQuestionnaireType(caze.getTreatmentCode());
    } else {
      questionnaireType = Integer.parseInt(caseDetails.getQuestionnaireType());
    }

    RestTemplate restTemplate = new RestTemplate();
    UriComponents uriComponents = createUriComponents(questionnaireType);
    ResponseEntity<UacQidDTO> responseEntity =
        restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, null, UacQidDTO.class);
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
