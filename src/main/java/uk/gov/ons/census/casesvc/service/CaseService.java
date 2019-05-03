package uk.gov.ons.census.casesvc.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.List;
import java.util.Optional;
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

  public Case findCaseByCaseId(UUID caseId) {
    log.debug("Entering findCaseByCaseId");

    Optional<Case> opt = Optional.ofNullable(caseRepo.findByCaseId(caseId));

    if (opt.isPresent()) {
      return opt.get();
    } else {
      return new Case();
    }
  }

  public List<Case> findCasesByUPRN(String uprn) {
    log.debug("Entering findCasesByUPRN");

    List<Case> cazes = caseRepo.findByuprn(uprn);

    return cazes;
  }

  public Case findCaseByReference(long reference) {
    log.debug("Entering findCaseByReference");

    Optional<Case> opt = Optional.ofNullable(caseRepo.findByCaseRef(reference));

    if (opt.isPresent()) {
      return opt.get();
    } else {
      return new Case();
    }
  }
}
