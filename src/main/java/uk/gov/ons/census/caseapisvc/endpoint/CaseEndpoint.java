package uk.gov.ons.census.caseapisvc.endpoint;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.gov.ons.census.caseapisvc.model.dto.*;
import uk.gov.ons.census.caseapisvc.service.CaseService;
import uk.gov.ons.census.common.model.entity.Case;
import uk.gov.ons.census.common.model.entity.Event;
import uk.gov.ons.census.common.model.entity.UacQidLink;

@RestController
@RequestMapping(value = "/cases")
@Timed
@Tag(name = "Case Endpoint", description = "Services for querying and retrieving census cases")
public class CaseEndpoint {
  private final CaseService caseService;

  @Autowired
  public CaseEndpoint(CaseService caseService) {
    this.caseService = caseService;
  }

  @GetMapping(value = "/{id}")
  @Operation(
      summary = "Find case by ID",
      description = "Retrieves a single case container record matching the specified UUID.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Case record found successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CaseContainerDTO.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Case record not found",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error occurred",
            content = @Content)
      })
  public CaseContainerDTO findCaseById(
      @Parameter(description = "Unique UUID of the case", required = true) @PathVariable("id")
          UUID id,
      @Parameter(description = "Flag indicating whether to include case events")
          @RequestParam(value = "caseEvents", required = false, defaultValue = "false")
          boolean caseEvents) {

    return buildCaseContainerDTO(caseService.findById(id), caseEvents);
  }

  @GetMapping(value = "/ref/{reference}")
  @Operation(
      summary = "Find case by Reference",
      description = "Retrieves a single case container record using the numeric case reference.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Case record found successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CaseContainerDTO.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Case reference not found",
            content = @Content)
      })
  public CaseContainerDTO findCaseByReference(
      @Parameter(description = "Unique numeric case reference identifier", required = true)
          @PathVariable("reference")
          long reference,
      @Parameter(description = "Flag indicating whether to include case events")
          @RequestParam(value = "caseEvents", required = false, defaultValue = "false")
          boolean caseEvents) {

    return buildCaseContainerDTO(caseService.findByReference(reference), caseEvents);
  }

  @GetMapping(value = "/uprn/{uprn}")
  @Operation(
      summary = "Find cases by UPRN",
      description = "Retrieves all cases associated with a Unique Property Reference Number.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Matching cases retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    array =
                        @ArraySchema(schema = @Schema(implementation = CaseContainerDTO.class))))
      })
  public List<CaseContainerDTO> findCasesByUPRN(
      @Parameter(description = "Unique Property Reference Number", required = true)
          @PathVariable("uprn")
          String uprn,
      @Parameter(description = "Flag indicating whether to include case events")
          @RequestParam(value = "caseEvents", required = false, defaultValue = "false")
          boolean caseEvents,
      @Parameter(description = "Filter results to valid addresses only")
          @RequestParam(value = "validAddressOnly", required = false, defaultValue = "false")
          boolean validAddressOnly) {

    List<CaseContainerDTO> caseContainerDTOs = new LinkedList<>();

    for (Case caze : caseService.findByUPRN(uprn, validAddressOnly)) {
      caseContainerDTOs.add(buildCaseContainerDTO(caze, caseEvents));
    }

    return caseContainerDTOs;
  }

  @GetMapping(value = "/postcode/{postcode}")
  @Operation(
      summary = "Find cases by Postcode",
      description = "Retrieves all cases located within the specified postcode area.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Matching cases retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    array =
                        @ArraySchema(schema = @Schema(implementation = CaseContainerDTO.class))))
      })
  public List<CaseContainerDTO> getCasesByPostcode(
      @Parameter(description = "Postal code identifier", required = true) @PathVariable("postcode")
          String postcode) {
    List<Case> cases = caseService.findByPostcode(postcode);
    List<CaseContainerDTO> caseContainerDTOs = new LinkedList<>();
    for (Case caze : cases) {
      caseContainerDTOs.add(buildCaseContainerDTO(caze, false));
    }
    return caseContainerDTOs;
  }

  @GetMapping(value = "/qid/{qid}")
  @Operation(
      summary = "Find case by Questionnaire ID (QID)",
      description = "Retrieves minimal case details linked to a specific questionnaire ID.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Case retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CaseContainerDTO.class))),
        @ApiResponse(responseCode = "404", description = "QID not found", content = @Content)
      })
  public CaseContainerDTO findCaseByQid(
      @Parameter(description = "Questionnaire Identifier", required = true) @PathVariable("qid")
          String qid) {
    Case caze = caseService.findCaseByQid(qid);
    CaseContainerDTO caseContainerDTO = new CaseContainerDTO();
    caseContainerDTO.setCaseId(caze.getId());
    caseContainerDTO.setAddressType(caze.getAddressType());

    return caseContainerDTO;
  }

  @GetMapping(value = "/case-details/{caseId}")
  @Operation(
      summary = "Get full case details by Case ID",
      description = "Retrieves complete detailed case attributes for a given case UUID.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Detailed case record retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CaseDetailsDTO.class))),
        @ApiResponse(responseCode = "404", description = "Case ID not found", content = @Content)
      })
  public CaseDetailsDTO getAllCaseDetailsByCaseId(
      @Parameter(description = "Unique UUID of the case", required = true) @PathVariable("caseId")
          UUID caseId) {
    Case caze = caseService.findById(caseId);
    return buildCaseDetailsDTO(caze);
  }

  private CaseContainerDTO buildCaseContainerDTO(Case caze, boolean includeCaseEvents) {

    CaseContainerDTO caseContainerDTO = mapCase(caze);

    List<CaseEventDTO> caseEvents = new LinkedList<>();

    if (includeCaseEvents) {
      List<UacQidLink> uacQidLinks = caze.getUacQidLinks();

      for (UacQidLink uacQidLink : uacQidLinks) {
        List<Event> events = uacQidLink.getEvents();

        for (Event event : events) {
          caseEvents.add(mapCaseEvent(event));
        }
      }
      if (caze.getEvents() != null) {
        for (Event event : caze.getEvents()) {
          caseEvents.add(mapCaseEvent(event));
        }
      }
    }

    caseContainerDTO.setCaseEvents(caseEvents);

    return caseContainerDTO;
  }

  private CaseContainerDTO mapCase(Case caze) {
    CaseContainerDTO caseContainerDTO = new CaseContainerDTO();
    caseContainerDTO.setCaseRef(caze.getCaseRef().toString());
    caseContainerDTO.setCaseId(caze.getId());
    caseContainerDTO.setInvalid(caze.isInvalid());
    caseContainerDTO.setCreatedDateTime(caze.getCreatedAt());
    caseContainerDTO.setLastUpdated(caze.getLastUpdatedAt());
    caseContainerDTO.setUprn(caze.getUprn());
    caseContainerDTO.setPostcode(caze.getPostcode());
    caseContainerDTO.setEstabType(caze.getEstabType());
    caseContainerDTO.setEstabUprn(caze.getEstabUprn());
    caseContainerDTO.setCollectionExerciseId(
        caze.getCollectionExercise() != null ? caze.getCollectionExercise().getId() : null);
    caseContainerDTO.setCaseType(caze.getCaseType());
    caseContainerDTO.setCreatedDateTime(caze.getCreatedAt());
    caseContainerDTO.setAddressLine1(caze.getAddressLine1());
    caseContainerDTO.setAddressLine2(caze.getAddressLine2());
    caseContainerDTO.setAddressLine3(caze.getAddressLine3());
    caseContainerDTO.setTownName(caze.getTownName());
    caseContainerDTO.setPostcode(caze.getPostcode());
    caseContainerDTO.setOrganisationName(caze.getOrganisationName());
    caseContainerDTO.setAddressLevel(caze.getAddressLevel());
    caseContainerDTO.setAbpCode(caze.getAbpCode());
    caseContainerDTO.setRegion(caze.getRegion());
    caseContainerDTO.setLatitude(caze.getLatitude());
    caseContainerDTO.setLongitude(caze.getLongitude());
    caseContainerDTO.setOa(caze.getOa());
    caseContainerDTO.setLsoa(caze.getLsoa());
    caseContainerDTO.setLastUpdated(caze.getLastUpdatedAt());
    caseContainerDTO.setMsoa(caze.getMsoa());
    caseContainerDTO.setSecureEstablishment(caze.isSecureEstablishment());
    caseContainerDTO.setAddressType(caze.getAddressType());
    caseContainerDTO.setLad(caze.getLad());
    return caseContainerDTO;
  }

  private CaseDetailsDTO mapCaseDetails(Case caze) {
    CaseDetailsDTO caseDetailsDTO = new CaseDetailsDTO();
    caseDetailsDTO.setCaseId(caze.getId());
    caseDetailsDTO.setCaseRef(caze.getCaseRef());
    caseDetailsDTO.setUprn(caze.getUprn());
    caseDetailsDTO.setEstabUprn(caze.getEstabUprn());
    caseDetailsDTO.setCaseType(caze.getCaseType());
    caseDetailsDTO.setAddressType(caze.getAddressType());
    caseDetailsDTO.setEstabType(caze.getEstabType());
    caseDetailsDTO.setAddressLevel(caze.getAddressLevel());
    caseDetailsDTO.setAbpCode(caze.getAbpCode());
    caseDetailsDTO.setOrganisationName(caze.getOrganisationName());
    caseDetailsDTO.setAddressLine1(caze.getAddressLine1());
    caseDetailsDTO.setAddressLine2(caze.getAddressLine2());
    caseDetailsDTO.setAddressLine3(caze.getAddressLine3());
    caseDetailsDTO.setTownName(caze.getTownName());
    caseDetailsDTO.setPostcode(caze.getPostcode());
    caseDetailsDTO.setLongitude(caze.getLongitude());
    caseDetailsDTO.setLatitude(caze.getLatitude());
    caseDetailsDTO.setOa(caze.getOa());
    caseDetailsDTO.setLsoa(caze.getLsoa());
    caseDetailsDTO.setMsoa(caze.getMsoa());
    caseDetailsDTO.setLad(caze.getLad());
    caseDetailsDTO.setRegion(caze.getRegion());
    caseDetailsDTO.setHtcWillingness(caze.getHtcWillingness());
    caseDetailsDTO.setHtcDigital(caze.getHtcDigital());
    caseDetailsDTO.setFieldCoordinatorId(caze.getFieldCoordinatorId());
    caseDetailsDTO.setFieldOfficerId(caze.getFieldOfficerId());
    caseDetailsDTO.setTreatmentCode(caze.getTreatmentCode());
    caseDetailsDTO.setCeExpectedCapacity(caze.getCeExpectedCapacity());
    caseDetailsDTO.setCollectionExerciseId(caze.getCollectionExercise().getId());
    caseDetailsDTO.setCreatedDateTime(caze.getCreatedAt());
    ;
    caseDetailsDTO.setReceiptReceived(caze.isReceiptReceived());
    caseDetailsDTO.setRefusalReceived(caze.getRefusalReceived());
    caseDetailsDTO.setInvalid(caze.isInvalid());
    caseDetailsDTO.setLastUpdated(caze.getLastUpdatedAt());
    caseDetailsDTO.setPrintBatch(caze.getPrintBatch());
    caseDetailsDTO.setSurveyLaunched(caze.isSurveyLaunched());

    caseDetailsDTO.setCaseId(caze.getId());
    caseDetailsDTO.setCreatedDateTime(caze.getCreatedAt());
    caseDetailsDTO.setLastUpdated(caze.getLastUpdatedAt());
    return caseDetailsDTO;
  }

  private CaseEventDTO mapCaseEvent(Event event) {
    CaseEventDTO caseEventDTO = new CaseEventDTO();
    caseEventDTO.setDescription(event.getDescription());
    caseEventDTO.setDateTime(event.getDateTime());
    caseEventDTO.setId(event.getId());
    caseEventDTO.setType(EventTypeDTO.valueOf(event.getType().name()));
    return caseEventDTO;
  }

  private CaseDetailsEventDTO mapCaseDetailsEvent(Event event) {
    CaseDetailsEventDTO caseDetailsEventDTO = new CaseDetailsEventDTO();
    caseDetailsEventDTO.setEventChannel(event.getChannel());
    caseDetailsEventDTO.setEventPayload(event.getPayload());
    caseDetailsEventDTO.setEventDescription(event.getDescription());
    caseDetailsEventDTO.setEventDate(event.getDateTime());
    caseDetailsEventDTO.setEventSource(event.getSource());
    caseDetailsEventDTO.setEventTransactionId(event.getCorrelationId());
    caseDetailsEventDTO.setEventType(event.getType().toString());
    caseDetailsEventDTO.setRmEventProcessed(event.getProcessedAt());
    caseDetailsEventDTO.setId(event.getId());
    caseDetailsEventDTO.setMessageTimestamp(event.getMessageTimestamp());

    return caseDetailsEventDTO;
  }

  private CaseDetailsDTO buildCaseDetailsDTO(Case caze) {

    CaseDetailsDTO caseDetailsDTO = mapCaseDetails(caze);

    List<CaseDetailsEventDTO> caseEvents = new LinkedList<>();

    List<UacQidLink> uacQidLinks = caze.getUacQidLinks();

    for (UacQidLink uacQidLink : uacQidLinks) {
      List<Event> events = uacQidLink.getEvents();

      for (Event event : events) {
        // RM_UAC_CREATED event redacted remove UACs
        // if (!event.getEventType().equals(EventType.RM_UAC_CREATED)) {
        caseEvents.add(mapCaseDetailsEvent(event));
        // }
      }
    }
    caseDetailsDTO.setEvents(caseEvents);

    return caseDetailsDTO;
  }
}
