package uk.gov.ons.census.caseapisvc.service;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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

  public List<Case> findByUPRN(String uprn) throws HttpClientErrorException {
    log.debug("Entering findByUPRN");

    return caseRepo
        .findByUprn(uprn)
        .orElseThrow(
            () ->
                new HttpClientErrorException(
                    NOT_FOUND, String.format("UPRN '%s' not found", uprn)));
  }

  public Case findByCaseId(UUID caseId) throws HttpClientErrorException {
    log.debug("Entering findByCaseId");

    return caseRepo
        .findByCaseId(caseId)
        .orElseThrow(
            () ->
                new HttpClientErrorException(
                    NOT_FOUND, String.format("Case Id '%s' not found", caseId.toString())));
  }

  public Case findByReference(Long reference) throws HttpClientErrorException {
    log.debug("Entering findByReference");

    return caseRepo
        .findByCaseRef(reference)
        .orElseThrow(
            () ->
                new HttpClientErrorException(
                    NOT_FOUND, String.format("Case Reference '%s' not found", reference)));
  }
}
