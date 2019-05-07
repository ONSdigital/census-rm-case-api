package uk.gov.ons.census.casesvc.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.census.casesvc.exception.CaseNotFoundException;
import uk.gov.ons.census.casesvc.model.entity.Case;
import uk.gov.ons.census.casesvc.model.repository.CaseRepository;

@Service
public class CaseService {
  private static final Logger log = LoggerFactory.getLogger(CaseService.class);

  private final CaseRepository caseRepo;

  @Autowired
  public CaseService(CaseRepository caseRepo) {
    this.caseRepo = caseRepo;
  }

  public Case findByCaseId(UUID caseId) {
    log.debug("Entering findByCaseId");

    return caseRepo
        .findByCaseId(caseId)
        .orElseThrow(
            () ->
                new CaseNotFoundException(
                    String.format("Case Id '%s' not found", caseId.toString())));
  }

  public List<Case> findByUPRN(String uprn) {
    log.debug("Entering findByUPRN");

    return caseRepo
        .findByuprn(uprn)
        .orElseThrow(() -> new CaseNotFoundException(String.format("UPRN '%s' not found", uprn)));
  }

  public Case findByReference(Long reference) {
    log.debug("Entering findByReference");

    return caseRepo
        .findByCaseRef(reference)
        .orElseThrow(
            () ->
                new CaseNotFoundException(
                    String.format("Case Reference '%s' not found", reference)));
  }
}
