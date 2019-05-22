package uk.gov.ons.census.caseapisvc.endpoint;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.census.caseapisvc.exception.CaseIdInvalidException;
import uk.gov.ons.census.caseapisvc.exception.CaseIdNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.CaseReferenceNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.UPRNNotFoundException;
import uk.gov.ons.census.caseapisvc.model.dto.CaseContainerDTO;
import uk.gov.ons.census.caseapisvc.model.dto.EventDTO;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.model.entity.Event;
import uk.gov.ons.census.caseapisvc.model.entity.UacQidLink;
import uk.gov.ons.census.caseapisvc.service.CaseService;

@RestController
@RequestMapping(value = "/cases")
public final class CaseEndpoint {
  private static final Logger log = LoggerFactory.getLogger(CaseEndpoint.class);

  private final CaseService caseService;
  private final MapperFacade mapperFacade;

  @Autowired
  public CaseEndpoint(CaseService caseService, MapperFacade mapperFacade) {
    this.caseService = caseService;
    this.mapperFacade = mapperFacade;
  }

  @GetMapping(value = "/uprn/{uprn}")
  public List<CaseContainerDTO> findCasesByUPRN(
      @PathVariable("uprn") String uprn,
      @RequestParam(value = "caseEvents", required = false, defaultValue = "false")
          boolean caseEvents) {
    log.debug("Entering findByUPRN");

    List<CaseContainerDTO> caseContainerDTOs = new LinkedList<>();

    for (Case caze : caseService.findByUPRN(uprn)) {
      caseContainerDTOs.add(buildCaseContainerDTO(caze, caseEvents));
    }

    return caseContainerDTOs;
  }

  @GetMapping(value = "/{caseId}")
  public CaseContainerDTO findCaseByCaseId(
      @PathVariable("caseId") String caseId,
      @RequestParam(value = "caseEvents", required = false, defaultValue = "false")
          boolean caseEvents) {
    log.debug("Entering findByCaseId");

    return buildCaseContainerDTO(caseService.findByCaseId(caseId), caseEvents);
  }

  @GetMapping(value = "/ref/{reference}")
  public CaseContainerDTO findCaseByReference(
      @PathVariable("reference") long reference,
      @RequestParam(value = "caseEvents", required = false, defaultValue = "false")
          boolean caseEvents) {
    log.debug("Entering findByReference");

    return buildCaseContainerDTO(caseService.findByReference(reference), caseEvents);
  }

  @ExceptionHandler({
    UPRNNotFoundException.class,
    CaseIdNotFoundException.class,
    CaseIdInvalidException.class,
    CaseReferenceNotFoundException.class
  })
  public void handleCaseIdNotFoundAndInvalid(HttpServletResponse response) throws IOException {
    response.sendError(NOT_FOUND.value());
  }

  private CaseContainerDTO buildCaseContainerDTO(Case caze, boolean includeCaseEvents) {

    CaseContainerDTO caseContainerDTO = this.mapperFacade.map(caze, CaseContainerDTO.class);
    List<EventDTO> caseEvents = new LinkedList<>();

    if (includeCaseEvents) {
      List<UacQidLink> uacQidLinks = caze.getUacQidLinks();

      for (UacQidLink uacQidLink : uacQidLinks) {
        List<Event> events = uacQidLink.getEvents();

        for (Event event : events) {
          caseEvents.add(this.mapperFacade.map(event, EventDTO.class));
        }
      }
    }

    caseContainerDTO.setCaseEvents(caseEvents);

    return caseContainerDTO;
  }
}
