package uk.gov.ons.census.caseapisvc.endpoint;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.census.caseapisvc.client.UacQidServiceClient;
import uk.gov.ons.census.caseapisvc.model.dto.CaseDetailsDTO;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidDTO;

@RestController
@RequestMapping(value = "/uacqid")
public class UacQidEndpoint {

  private final UacQidServiceClient uacQidServiceClient;

  public UacQidEndpoint(UacQidServiceClient uacQidServiceClient) {
    this.uacQidServiceClient = uacQidServiceClient;
  }

  @PostMapping(path = "/create", consumes = "application/json", produces = "application/json")
  public ResponseEntity<UacQidDTO> createAndLinkUacQid(@RequestBody CaseDetailsDTO caseDetails) {
    int questionnaireType = Integer.parseInt(caseDetails.getQuestionnaireType());
    UacQidDTO uacQid = uacQidServiceClient.generateUacQid(questionnaireType);
    return ResponseEntity.status(HttpStatus.CREATED).body(uacQid);
  }
}
