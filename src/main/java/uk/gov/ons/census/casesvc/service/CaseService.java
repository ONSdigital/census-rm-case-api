package uk.gov.ons.census.casesvc.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    return caseRepo.findByCaseId(caseId);
  }

  public List<Case> findByUPRN(String uprn) {
    log.debug("Entering findByUPRN");

    return caseRepo.findByuprn(uprn);
  }

  public Case findByReference(long reference) {
    log.debug("Entering findByReference");

    return caseRepo.findByCaseRef(reference);
  }
}
