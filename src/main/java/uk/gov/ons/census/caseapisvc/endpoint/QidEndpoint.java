package uk.gov.ons.census.caseapisvc.endpoint;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import io.micrometer.core.annotation.Timed;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.census.caseapisvc.exception.CaseIdNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.QidNotFoundException;
import uk.gov.ons.census.caseapisvc.model.dto.QidLink;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.model.entity.UacQidLink;
import uk.gov.ons.census.caseapisvc.service.CaseService;
import uk.gov.ons.census.caseapisvc.service.UacQidService;

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
  public QidLink getUacQidLinkByQid(@PathVariable("qid") String qid) {
    UacQidLink uacQidLink = uacQidService.findUacQidLinkByQid(qid);
    QidLink qidDetails = new QidLink();
    qidDetails.setQuestionnaireId(uacQidLink.getQid());
    if (uacQidLink.getCaze() != null) {
      qidDetails.setCaseId(uacQidLink.getCaze().getCaseId());
    }
    return qidDetails;
  }

  @PutMapping(value = "/link")
  public void putQidLinkToCase(@RequestBody QidLink qidLink) {
    UacQidLink uacQidLink = uacQidService.findUacQidLinkByQid(qidLink.getQuestionnaireId());
    Case caseToLink = caseService.findByCaseId(qidLink.getCaseId());

    uacQidService.buildAndSendQuestionnaireLinkedEvent(uacQidLink, caseToLink);
  }

  @ExceptionHandler({CaseIdNotFoundException.class, QidNotFoundException.class})
  public void handleCaseIdNotFoundAndInvalid(HttpServletResponse response) throws IOException {
    response.sendError(NOT_FOUND.value());
  }
}
