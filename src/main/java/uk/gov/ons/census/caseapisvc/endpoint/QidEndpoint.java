package uk.gov.ons.census.caseapisvc.endpoint;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.ons.census.caseapisvc.exception.CaseIdNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.CaseReferenceNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.QidNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.UPRNNotFoundException;
import uk.gov.ons.census.caseapisvc.model.dto.QidLink;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.model.entity.UacQidLink;
import uk.gov.ons.census.caseapisvc.service.CaseService;
import uk.gov.ons.census.caseapisvc.service.UacQidService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping(value = "/qids")
@Timed
public class QidEndpoint {
  private UacQidService uacQidService;
  private CaseService caseService;

  @Autowired
  public QidEndpoint(UacQidService uacQidService, CaseService caseService) {
    this.uacQidService = uacQidService;
    this.caseService = caseService;
  }

  @GetMapping(value = "/{qid}")
  public QidLink findUacQidLinkByQid(@PathVariable("qid") String qid) {
    UacQidLink uacQidLink = getUacQidLinkByQid(qid);
    QidLink qidDetails = new QidLink();
    qidDetails.setQuestionnaireId(uacQidLink.getQid());
    if (uacQidLink.getCaze() != null) {
      qidDetails.setCaseId(uacQidLink.getCaze().getCaseId());
    }
    return qidDetails;
  }

  private UacQidLink getUacQidLinkByQid(String qid) {
    Optional<UacQidLink> uacQidLinkOptional = uacQidService.findUacQidLinkByQid(qid);
    return uacQidLinkOptional.orElseThrow(() -> new QidNotFoundException(qid));
  }

  @PutMapping(value = "/link")
  public void putQidLink(@RequestBody QidLink qidLink) {
    UacQidLink uacQidLink = getUacQidLinkByQid(qidLink.getQuestionnaireId());
    Case caseToLink =  caseService.findByCaseId(qidLink.getCaseId()); // Throws CaseIdNotFoundException if not found

    uacQidService.buildAndSendQuestionnaireLinkedEvent(uacQidLink, caseToLink);

  }

  @ExceptionHandler({CaseIdNotFoundException.class, QidNotFoundException.class})
  public void handleCaseIdNotFoundAndInvalid(HttpServletResponse response) throws IOException {
    response.sendError(NOT_FOUND.value());
  }
}
