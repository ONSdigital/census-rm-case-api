package uk.gov.ons.census.caseapisvc.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.census.caseapisvc.model.dto.CaseDetailsDTO;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedPayloadDTO;
import uk.gov.ons.census.caseapisvc.service.UacQidService;

@RestController
@RequestMapping(value = "/uacqid")
public class UacQidEndpoint {

  private static final Logger log = LoggerFactory.getLogger(UacQidEndpoint.class);

  private final UacQidService uacQidService;

  public UacQidEndpoint(UacQidService uacQidService) {
    this.uacQidService = uacQidService;
  }

  @PostMapping(path = "/create", consumes = "application/json", produces = "application/json")
  public ResponseEntity<UacQidCreatedPayloadDTO> createAndLinkUacQid(
      @RequestBody CaseDetailsDTO caseDetails) {
    int questionnaireType = Integer.parseInt(caseDetails.getQuestionnaireType());
    log.with("caseId", caseDetails.getCaseId()).debug("Generating UAC QID pair for case");
    UacQidCreatedPayloadDTO uacQidCreatedPayload =
        uacQidService.createAndLinkUacQid(caseDetails.getCaseId().toString(), questionnaireType);
    uacQidService.sendUacQidCreatedEvent(uacQidCreatedPayload);
    return ResponseEntity.status(HttpStatus.CREATED).body(uacQidCreatedPayload);
  }
}
