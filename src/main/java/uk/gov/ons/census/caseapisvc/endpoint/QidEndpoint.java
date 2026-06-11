package uk.gov.ons.census.caseapisvc.endpoint;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.census.caseapisvc.model.dto.NewQidLink;
import uk.gov.ons.census.caseapisvc.model.dto.QidLink;
import uk.gov.ons.census.caseapisvc.service.CaseService;
import uk.gov.ons.census.caseapisvc.service.UacQidService;
import uk.gov.ons.census.common.model.entity.Case;
import uk.gov.ons.census.common.model.entity.UacQidLink;

@RestController
@RequestMapping(value = "/qids")
@Timed
public class QidEndpoint {
  private final UacQidService uacQidService;
  private final CaseService caseService;

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
      qidDetails.setCaseId(uacQidLink.getCaze().getId());
    }
    return qidDetails;
  }

  @PutMapping(value = "/link")
  public void putQidLinkToCase(@RequestBody NewQidLink newQidLink) {
    UacQidLink uacQidLink =
        uacQidService.findUacQidLinkByQid(newQidLink.getQidLink().getQuestionnaireId());
    Case caseToLink = caseService.findById(newQidLink.getQidLink().getCaseId());

    uacQidService.buildAndSendQuestionnaireLinkedEvent(uacQidLink, caseToLink, newQidLink);
  }
}
