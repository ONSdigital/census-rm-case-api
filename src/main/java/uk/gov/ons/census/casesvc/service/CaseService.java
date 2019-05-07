package uk.gov.ons.census.casesvc.service;

import static uk.gov.ons.ctp.common.error.CTPException.Fault.RESOURCE_NOT_FOUND;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.census.casesvc.model.entity.Case;
import uk.gov.ons.census.casesvc.model.repository.CaseRepository;
import uk.gov.ons.ctp.common.error.CTPException;

@Service
public class CaseService {
  private static final Logger log = LoggerFactory.getLogger(CaseService.class);

  private final CaseRepository caseRepo;

  @Autowired
  public CaseService(CaseRepository caseRepo) {
    this.caseRepo = caseRepo;
  }

  public List<Case> findByUPRN(String uprn) throws CTPException {
    log.debug("Entering findByUPRN");

    return caseRepo
        .findByuprn(uprn)
        .orElseThrow(
            () -> new CTPException(RESOURCE_NOT_FOUND, String.format("UPRN '%s' not found", uprn)));
  }

  public Case findByCaseId(UUID caseId) throws CTPException {
    log.debug("Entering findByCaseId");

    return caseRepo
        .findByCaseId(caseId)
        .orElseThrow(
            () ->
                new CTPException(
                    RESOURCE_NOT_FOUND,
                    String.format("Case Id '%s' not found", caseId.toString())));
  }

  public Case findByReference(Long reference) throws CTPException {
    log.debug("Entering findByReference");

    return caseRepo
        .findByCaseRef(reference)
        .orElseThrow(
            () ->
                new CTPException(
                    RESOURCE_NOT_FOUND, String.format("Case Reference '%s' not found", reference)));
  }
}
