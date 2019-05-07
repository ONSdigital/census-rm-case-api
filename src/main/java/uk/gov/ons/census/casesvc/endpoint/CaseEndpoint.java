package uk.gov.ons.census.casesvc.endpoint;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import uk.gov.ons.census.casesvc.config.ApiError;
import uk.gov.ons.census.casesvc.model.dto.CaseContainerDTO;
import uk.gov.ons.census.casesvc.model.dto.EventDTO;
import uk.gov.ons.census.casesvc.model.entity.Case;
import uk.gov.ons.census.casesvc.model.entity.Event;
import uk.gov.ons.census.casesvc.model.entity.UacQidLink;
import uk.gov.ons.census.casesvc.service.CaseService;
import uk.gov.ons.ctp.common.error.CTPException;

@RestController
@Api(
    value = "/cases",
    description = "Provides features and functions required by the Contact Centre.")
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

  @ApiOperation(
      value = "Search for a case by address uprn.",
      notes = "Returns cases",
      responseContainer = "List",
      produces = APPLICATION_JSON_VALUE)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Success. A json object return of matched cases"),
        @ApiResponse(
            code = 400,
            message =
                "Bad request. Indicates an issue with the request. Further details are provided in the response."),
        @ApiResponse(
            code = 401,
            message = "Unauthorised. The API key provided with the request is invalid."),
        @ApiResponse(code = 404, message = "UPRN '{uprn}' not found"),
        @ApiResponse(
            code = 429,
            message = "Server too busy. The ONS API is experiencing exceptional load."),
        @ApiResponse(
            code = 500,
            message =
                "Internal server error. Failed to process the request due to an internal error.")
      })
  @GetMapping(value = "/uprn/{uprn}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<List<CaseContainerDTO>> findCasesByUPRN(
      @ApiParam(value = "Lookup a case by the UPRN", required = true) @PathVariable("uprn")
          String uprn,
      @ApiParam(value = "true if case events are additionally required")
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

  @ApiOperation(
      value = "Search for a case by its Id.",
      notes = "Returns a case",
      response = CaseContainerDTO.class,
      produces = APPLICATION_JSON_VALUE)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Success. A json object return of matched case."),
        @ApiResponse(
            code = 400,
            message =
                "Bad request. Indicates an issue with the request. Further details are provided in the response."),
        @ApiResponse(
            code = 401,
            message = "Unauthorised. The API key provided with the request is invalid."),
        @ApiResponse(code = 404, message = "Case Reference '{CaseId}' not found"),
        @ApiResponse(
            code = 429,
            message = "Server too busy. The ONS API is experiencing exceptional load."),
        @ApiResponse(
            code = 500,
            message =
                "Internal server error. Failed to process the request due to an internal error.")
      })
  @GetMapping(value = "/{caseId}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<CaseContainerDTO> findCaseByCaseId(
      @ApiParam(value = "Lookup a case by the case uuid", required = true) @PathVariable("caseId")
          UUID caseId,
      @ApiParam(value = "true if case events are additionally required")
          @RequestParam(value = "caseEvents", required = false, defaultValue = "false")
          Boolean caseEvents)
      throws Exception {
    log.debug("Entering findByCaseId");

    return ResponseEntity.ok(
        buildCaseFoundResponseDTO(caseService.findByCaseId(caseId), caseEvents));
  }

  @ApiOperation(
      value = "Search for a case by its reference.",
      notes = "Returns a case by the case reference if available.",
      response = CaseContainerDTO.class,
      produces = APPLICATION_JSON_VALUE)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Success. A json object return of matched case."),
        @ApiResponse(
            code = 400,
            message =
                "Bad request. Indicates an issue with the request. Further details are provided in the response."),
        @ApiResponse(
            code = 401,
            message = "Unauthorised. The API key provided with the request is invalid."),
        @ApiResponse(code = 404, message = "Case Reference '{reference}' not found"),
        @ApiResponse(
            code = 429,
            message = "Server too busy. The ONS API is experiencing exceptional load."),
        @ApiResponse(
            code = 500,
            message =
                "Internal server error. Failed to process the request due to an internal error.")
      })
  @GetMapping(value = "/ref/{reference}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<CaseContainerDTO> findCaseByReference(
      @ApiParam(value = "Case reference e.g 123000001", required = true) @PathVariable("reference")
          Long reference,
      @ApiParam(value = "true if case events are additionally required")
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
