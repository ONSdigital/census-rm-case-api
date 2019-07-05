package uk.gov.ons.census.caseapisvc.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.census.caseapisvc.exception.CaseIdInvalidException;
import uk.gov.ons.census.caseapisvc.exception.CaseIdNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.CaseReferenceNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.UPRNNotFoundException;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.model.repository.CaseRepository;

@Service
public class CaseService {
  private static final Logger log = LoggerFactory.getLogger(CaseService.class);

  private final CaseRepository caseRepo;

  @Autowired
  public CaseService(CaseRepository caseRepo) {
    this.caseRepo = caseRepo;
  }

  public List<Case> findByUPRN(String uprn) {
    log.debug("Entering findByUPRN");

    return caseRepo.findByUprn(uprn).orElseThrow(() -> new UPRNNotFoundException(uprn));
  }

  public Case findByCaseId(String caseId) {
    log.debug("Entering findByCaseId");

    UUID caseIdUUID = validateAndConvertCaseIdToUUID(caseId);

    return caseRepo
        .findByCaseId(caseIdUUID)
        .orElseThrow(() -> new CaseIdNotFoundException(caseIdUUID.toString()));
  }

  public Case findByReference(int reference) {
    log.debug("Entering findByReference");

    return caseRepo
        .findByCaseRef(reference)
        .orElseThrow(() -> new CaseReferenceNotFoundException(reference));
  }

  private UUID validateAndConvertCaseIdToUUID(String caseId) {
    UUID caseIdUUID;

    try {
      caseIdUUID = UUID.fromString(caseId);
    } catch (IllegalArgumentException iae) {
      throw new CaseIdInvalidException(caseId);
    }

    return caseIdUUID;
  }
}
