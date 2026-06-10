package uk.gov.ons.census.caseapisvc.endpoint;

import io.micrometer.core.annotation.Timed;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.census.caseapisvc.model.dto.*;
import uk.gov.ons.census.caseapisvc.service.CaseService;
import uk.gov.ons.census.common.model.entity.Case;
import uk.gov.ons.census.common.model.entity.Event;
import uk.gov.ons.census.common.model.entity.UacQidLink;

@RestController
@RequestMapping(value = "/cases")
@Timed
public class CaseEndpoint {
  private final CaseService caseService;

  @Autowired
  public CaseEndpoint(CaseService caseService) {
    this.caseService = caseService;
  }

  @GetMapping(value = "/{id}")
  public CaseContainerDTO findCaseById(
      @PathVariable("id") UUID id,
      @RequestParam(value = "caseEvents", required = false, defaultValue = "false")
          boolean caseEvents) {

    return buildCaseContainerDTO(caseService.findById(id), caseEvents);
  }

  @GetMapping(value = "/ref/{reference}")
  public CaseContainerDTO findCaseByReference(
      @PathVariable("reference") long reference,
      @RequestParam(value = "caseEvents", required = false, defaultValue = "false")
          boolean caseEvents) {

    return buildCaseContainerDTO(caseService.findByReference(reference), caseEvents);
  }

  @GetMapping(value = "/uprn/{uprn}")
  public List<CaseContainerDTO> findCasesByUPRN(
      @PathVariable("uprn") String uprn,
      @RequestParam(value = "caseEvents", required = false, defaultValue = "false")
          boolean caseEvents,
      @RequestParam(value = "validAddressOnly", required = false, defaultValue = "false")
          boolean validAddressOnly) {

    List<CaseContainerDTO> caseContainerDTOs = new LinkedList<>();

    for (Case caze : caseService.findByUPRN(uprn, validAddressOnly)) {
      caseContainerDTOs.add(buildCaseContainerDTO(caze, caseEvents));
    }

    return caseContainerDTOs;
  }

  @GetMapping(value = "/postcode/{postcode}")
  public List<CaseContainerDTO> getCasesByPostcode(@PathVariable("postcode") String postcode) {
    List<Case> cases = caseService.findByPostcode(postcode);
    return cases.stream().map(c -> buildCaseContainerDTO(c, false)).collect(Collectors.toList());
  }

  @GetMapping(value = "/qid/{qid}")
  public CaseContainerDTO findCaseByQid(@PathVariable("qid") String qid) {
    Case caze = caseService.findCaseByQid(qid);
    CaseContainerDTO caseContainerDTO = new CaseContainerDTO();
    caseContainerDTO.setCaseId(caze.getId());
    caseContainerDTO.setAddressType(caze.getAddressType());

    return caseContainerDTO;
  }

  @GetMapping(value = "/case-details/{caseId}")
  public CaseDetailsDTO getAllCaseDetailsByCaseId(@PathVariable("caseId") UUID caseId) {
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
    caseContainerDTO.setAddressInvalid(caze.isInvalid());
    caseContainerDTO.setCreatedDateTime(caze.getCreatedAt());
    caseContainerDTO.setLastUpdated(caze.getLastUpdatedAt());
    // caseContainerDTO.setRefusalReceived(caze.getRefusalReceived());
    // caseContainerDTO.setSample(caze.getSample());
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
    // caseDetailsDTO.setActionPlanId(caze.getActionPlanId());
    // caseDetailsDTO.setSurvey(caze.getSurvey());
    caseDetailsDTO.setCreatedDateTime(caze.getCreatedAt());
    // caseDetailsDTO.setEvents(caze.getEvents());
    caseDetailsDTO.setReceiptReceived(caze.isReceiptReceived());
    caseDetailsDTO.setRefusalReceived(caze.getRefusalReceived());
    caseDetailsDTO.setAddressInvalid(caze.isInvalid());
    caseDetailsDTO.setLastUpdated(caze.getLastUpdatedAt());
    // caseDetailsDTO.setHandDelivery();
    // caseDetailsDTO.setSkeleton();
    caseDetailsDTO.setPrintBatch(caze.getPrintBatch());
    caseDetailsDTO.setSurveyLaunched(caze.isSurveyLaunched());

    caseDetailsDTO.setCaseId(caze.getId());
    caseDetailsDTO.setAddressInvalid(caze.isInvalid());
    caseDetailsDTO.setCreatedDateTime(caze.getCreatedAt());
    caseDetailsDTO.setLastUpdated(caze.getLastUpdatedAt());
    // caseContainerDTO.setRefusalReceived(caze.getRefusalReceived());
    // caseContainerDTO.setSample(caze.getSample());
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
