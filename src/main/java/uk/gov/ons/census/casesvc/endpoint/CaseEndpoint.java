package uk.gov.ons.census.casesvc.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.census.casesvc.model.dto.CaseContainerDTO;
import uk.gov.ons.census.casesvc.model.dto.EmptyResponseDTO;
import uk.gov.ons.census.casesvc.model.dto.EventDTO;
import uk.gov.ons.census.casesvc.model.entity.Case;
import uk.gov.ons.census.casesvc.model.entity.Event;
import uk.gov.ons.census.casesvc.model.entity.UacQidLink;
import uk.gov.ons.census.casesvc.service.CaseService;

@RestController
@RequestMapping(value = "/cases", produces = "application/json")
public final class CaseEndpoint {
  private static final Logger log = LoggerFactory.getLogger(CaseEndpoint.class);

  private final CaseService caseService;
  private final MapperFacade mapperFacade;

  @Autowired
  public CaseEndpoint(
      CaseService caseService, @Qualifier("caseSvcBeanMapper") MapperFacade mapperFacade) {
    this.caseService = caseService;
    this.mapperFacade = mapperFacade;
  }

  @GetMapping(value = "/{caseId}")
  public ResponseEntity<CaseContainerDTO> findCaseByCaseId(@PathVariable("caseId") UUID caseId) {
    log.debug("Entering findByCaseId");

    Optional<Case> caze = Optional.ofNullable(caseService.findByCaseId(caseId));

    if (caze.isEmpty()) {
      return new ResponseEntity(buildCaseNotFoundResponseDTO(), HttpStatus.OK);
    }

    return ResponseEntity.ok(buildCaseFoundResponseDTO(caze.get()));
  }

  @GetMapping(value = "/uprn/{uprn}")
  public ResponseEntity<List<CaseContainerDTO>> findCasesByUPRN(@PathVariable("uprn") String uprn) {
    log.debug("Entering findByUPRN");

    Optional<List<Case>> cazes = Optional.ofNullable(caseService.findByUPRN(uprn));

    if (cazes.isPresent() && cazes.get().size() == 0) {
      return new ResponseEntity(buildCaseNotFoundResponseDTO(), HttpStatus.OK);
    }

    List<CaseContainerDTO> caseContainerDTOs = new ArrayList<>();

    for (Case caze : cazes.get()) {
      caseContainerDTOs.add(buildCaseFoundResponseDTO(caze));
    }

    return ResponseEntity.ok(caseContainerDTOs);
  }

  @GetMapping(value = "/ref/{reference}")
  public ResponseEntity<CaseContainerDTO> findCaseByReference(
      @PathVariable("reference") long reference) {
    log.debug("Entering findByReference");

    Optional<Case> caze = Optional.ofNullable(caseService.findByReference(reference));

    if (caze.isEmpty()) {
      return new ResponseEntity(buildCaseNotFoundResponseDTO(), HttpStatus.OK);
    }

    return ResponseEntity.ok(buildCaseFoundResponseDTO(caze.get()));
  }

  private EmptyResponseDTO buildCaseNotFoundResponseDTO() {
    return new EmptyResponseDTO();
  }

  private CaseContainerDTO buildCaseFoundResponseDTO(Case caze) {

    List<EventDTO> caseEvents = new LinkedList<>();
    List<UacQidLink> uacQidLinks = caze.getUacQidLinks();

    for (UacQidLink uacQidLink : uacQidLinks) {
      List<Event> events = uacQidLink.getEvents();

      for (Event event : events) {
        caseEvents.add(this.mapperFacade.map(event, EventDTO.class));
      }
    }

    CaseContainerDTO caseContainerDTO = this.mapperFacade.map(caze, CaseContainerDTO.class);
    caseContainerDTO.setCaseEvents(caseEvents);

    return caseContainerDTO;
  }
}
