package uk.gov.ons.census.casesvc.endpoint;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.ons.census.casesvc.error.ApiError;
import uk.gov.ons.census.casesvc.model.dto.CaseContainerDTO;
import uk.gov.ons.census.casesvc.model.dto.EventDTO;
import uk.gov.ons.census.casesvc.model.entity.Case;
import uk.gov.ons.census.casesvc.model.entity.Event;
import uk.gov.ons.census.casesvc.model.entity.UacQidLink;
import uk.gov.ons.census.casesvc.service.CaseService;
import uk.gov.ons.ctp.common.error.CTPException;

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

  @GetMapping(value = "/uprn/{uprn}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<List<CaseContainerDTO>> findCasesByUPRN(
      @PathVariable("uprn") String uprn,
      @RequestParam(value = "caseEvents", required = false, defaultValue = "false")
          Boolean caseEvents)
      throws CTPException {
    log.debug("Entering findByUPRN");

    List<CaseContainerDTO> caseContainerDTOs = new ArrayList<>();

    for (Case caze : caseService.findByUPRN(uprn)) {
      caseContainerDTOs.add(buildCaseFoundResponseDTO(caze, caseEvents));
    }

    return ResponseEntity.ok(caseContainerDTOs);
  }

  @GetMapping(value = "/{caseId}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<CaseContainerDTO> findCaseByCaseId(
      @PathVariable("caseId") UUID caseId,
      @RequestParam(value = "caseEvents", required = false, defaultValue = "false")
          Boolean caseEvents)
      throws Exception {
    log.debug("Entering findByCaseId");

    return ResponseEntity.ok(
        buildCaseFoundResponseDTO(caseService.findByCaseId(caseId), caseEvents));
  }

  @GetMapping(value = "/ref/{reference}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<CaseContainerDTO> findCaseByReference(
      @PathVariable("reference") Long reference,
      @RequestParam(value = "caseEvents", required = false, defaultValue = "false")
          Boolean caseEvents)
      throws CTPException {
    log.debug("Entering findByReference");

    return ResponseEntity.ok(
        buildCaseFoundResponseDTO(caseService.findByReference(reference), caseEvents));
  }

  @ExceptionHandler(HttpClientErrorException.class)
  public ResponseEntity<Object> handleHttpClientErrorException(HttpClientErrorException hcee) {
    HttpStatus statusCode = hcee.getStatusCode();

    switch (statusCode) {
      case BAD_REQUEST:
        return buildResponseEntity(
            statusCode,
            "Bad request. Indicates an issue with the request. Further details are provided in the response.");
      case UNAUTHORIZED:
        return buildResponseEntity(
            statusCode, "Unauthorised. The API key provided with the request is invalid.");
      case TOO_MANY_REQUESTS:
        return buildResponseEntity(
            statusCode, "Server too busy. The ONS API is experiencing exceptional load.");
      default:
        return buildResponseEntity(statusCode, "Unexpected Internal Server Error");
    }
  }

  private ResponseEntity<Object> buildResponseEntity(HttpStatus statusCode, String message) {
    return new ResponseEntity(new ApiError(statusCode, message), statusCode);
  }

  private CaseContainerDTO buildCaseFoundResponseDTO(Case caze, Boolean includeCaseEvents) {

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
