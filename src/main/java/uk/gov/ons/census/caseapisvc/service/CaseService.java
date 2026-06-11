package uk.gov.ons.census.caseapisvc.service;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.ons.census.caseapisvc.model.repository.CaseRepository;
import uk.gov.ons.census.caseapisvc.model.repository.UacQidLinkRepository;
import uk.gov.ons.census.common.model.entity.Case;
import uk.gov.ons.census.common.model.entity.UacQidLink;

@Service
public class CaseService {

  private final CaseRepository caseRepository;
  private final UacQidLinkRepository uacQidLinkRepository;

  public CaseService(CaseRepository caseRepository, UacQidLinkRepository uacQidLinkRepository) {
    this.caseRepository = caseRepository;
    this.uacQidLinkRepository = uacQidLinkRepository;
  }

  public Case findById(UUID id) {

    return caseRepository
        .findById(id)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("Case Id '%s' not found", id)));
  }

  public Case findByReference(long reference) {

    return caseRepository
        .findByCaseRef(reference)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("Case Reference '%s' not found", reference)));
  }

  public List<Case> findByUPRN(String uprn, boolean validAddressOnly) {

    if (validAddressOnly) {
      return caseRepository
          .findByUprnAndInvalidFalse(uprn)
          .orElseThrow(
              () ->
                  new ResponseStatusException(
                      HttpStatus.NOT_FOUND, String.format("UPRN '%s' not found", uprn)));
    } else {
      return caseRepository
          .findByUprn(uprn)
          .orElseThrow(
              () ->
                  new ResponseStatusException(
                      HttpStatus.NOT_FOUND, String.format("UPRN  '%s' not found", uprn)));
    }
  }

  public Case findCaseByQid(String qid) {
    UacQidLink uacQidLink =
        uacQidLinkRepository
            .findByQid(qid)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, String.format("QID  '%s' not found", qid)));

    if (uacQidLink.getCaze() == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("Case for QID  '%s' not found", qid));
    }

    return uacQidLink.getCaze();
  }

  public List<Case> findByPostcode(String postcode) {
    List<Case> cazeList = caseRepository.findByPostcode(postcode);
    return cazeList;
  }
}
