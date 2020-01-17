package uk.gov.ons.census.caseapisvc.endpoint;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static uk.gov.ons.census.caseapisvc.service.UacQidService.calculateQuestionnaireType;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.ons.census.caseapisvc.exception.CaseIdInvalidException;
import uk.gov.ons.census.caseapisvc.exception.CaseIdNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.CaseReferenceNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.QidNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.UPRNNotFoundException;
import uk.gov.ons.census.caseapisvc.model.dto.CaseContainerDTO;
import uk.gov.ons.census.caseapisvc.model.dto.CaseEventDTO;
import uk.gov.ons.census.caseapisvc.model.dto.QidDTO;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedPayloadDTO;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidDTO;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.model.entity.Event;
import uk.gov.ons.census.caseapisvc.model.entity.UacQidLink;
import uk.gov.ons.census.caseapisvc.service.CaseService;
import uk.gov.ons.census.caseapisvc.service.UacQidService;

@RestController
@RequestMapping(value = "/cases")
public final class CaseEndpoint {

  private static final Logger log = LoggerFactory.getLogger(CaseEndpoint.class);

  private final CaseService caseService;
  private final MapperFacade mapperFacade;
  private UacQidService uacQidService;

  @Autowired
  public CaseEndpoint(
      CaseService caseService, MapperFacade mapperFacade, UacQidService uacQidService) {
    this.caseService = caseService;
    this.mapperFacade = mapperFacade;
    this.uacQidService = uacQidService;
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

  @GetMapping(value = "/qid/{qid}")
  public CaseContainerDTO findCaseByQid(@PathVariable("qid") String qid) {
    log.debug("Entering findByQid");
    Case caze = caseService.findCaseByQid(qid);
    CaseContainerDTO caseContainerDTO = new CaseContainerDTO();
    caseContainerDTO.setCaseId(caze.getCaseId().toString());
    caseContainerDTO.setAddressType(caze.getAddressType());

    return caseContainerDTO;
  }

  @GetMapping(value = "/ref/{reference}")
  public CaseContainerDTO findCaseByReference(
      @PathVariable("reference") int reference,
      @RequestParam(value = "caseEvents", required = false, defaultValue = "false")
          boolean caseEvents) {
    log.debug("Entering findByReference");

    return buildCaseContainerDTO(caseService.findByReference(reference), caseEvents);
  }

  @GetMapping(value = "/ccs/{caseId}/qid")
  public QidDTO findCCSQidByCaseId(@PathVariable("caseId") String caseId) {
    log.debug("Entering findByCaseId");
    UacQidLink ccsUacQidLink = caseService.findCCSUacQidLinkByCaseId(caseId);
    QidDTO qidDTO = new QidDTO();
    qidDTO.setQuestionnaireId(ccsUacQidLink.getQid());
    qidDTO.setActive(ccsUacQidLink.isActive());
    return qidDTO;
  }

  @GetMapping(value = "/{caseId}/qid")
  public UacQidDTO getNewQidByCaseId(
      @PathVariable("caseId") String caseId,
      @RequestParam(value = "individual", required = false, defaultValue = "false")
          boolean individual,
      @RequestParam(value = "individualCaseId", required = false) String individualCaseId) {
    log.debug("Entering getNewQidByCaseId");
    if (individualCaseId != null && individual) {
      return handleIndividualQidRequest(caseId, individualCaseId);

    } else if (individualCaseId != null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "IndividualCaseId requires individual flag to be true");
    }
    return handleQidRequest(caseId);
  }

  private UacQidDTO handleQidRequest(String caseId) {
    Case caze = caseService.findByCaseId(caseId);
    int questionnaireType =
        calculateQuestionnaireType(caze.getTreatmentCode(), caze.getAddressLevel());

    UacQidCreatedPayloadDTO uacQidCreatedPayload =
        uacQidService.createAndLinkUacQid(caze.getCaseId().toString(), questionnaireType);
    UacQidDTO uacQidDTO = new UacQidDTO();
    uacQidDTO.setQuestionnaireId(uacQidCreatedPayload.getQid());
    uacQidDTO.setUac(uacQidCreatedPayload.getUac());
    return uacQidDTO;
  }

  private UacQidDTO handleIndividualQidRequest(String caseId, String individualCaseId) {
    if (caseService.caseExistsByCaseId(individualCaseId)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          String.format("IndividualCaseId %s already exists", individualCaseId));
    }
    Case caze = caseService.findByCaseId(caseId);
    int questionnaireType =
        calculateQuestionnaireType(caze.getTreatmentCode(), caze.getAddressLevel(), true);

    UacQidCreatedPayloadDTO uacQidCreatedPayload =
        uacQidService.createAndLinkUacQid(individualCaseId, questionnaireType);

    caseService.buildAndSendHiTelephoneCaptureFulfilmentRequest(caseId, individualCaseId);
    UacQidDTO uacQidDTO = new UacQidDTO();
    uacQidDTO.setQuestionnaireId(uacQidCreatedPayload.getQid());
    uacQidDTO.setUac(uacQidCreatedPayload.getUac());
    return uacQidDTO;
  }

  @ExceptionHandler({
    UPRNNotFoundException.class,
    CaseIdNotFoundException.class,
    CaseIdInvalidException.class,
    CaseReferenceNotFoundException.class,
    QidNotFoundException.class
  })
  public void handleCaseIdNotFoundAndInvalid(HttpServletResponse response) throws IOException {
    response.sendError(NOT_FOUND.value());
  }

  private CaseContainerDTO buildCaseContainerDTO(Case caze, boolean includeCaseEvents) {

    CaseContainerDTO caseContainerDTO = this.mapperFacade.map(caze, CaseContainerDTO.class);
    caseContainerDTO.setSurveyType(caze.getSurvey());

    List<CaseEventDTO> caseEvents = new LinkedList<>();

    if (includeCaseEvents) {
      List<UacQidLink> uacQidLinks = caze.getUacQidLinks();

      for (UacQidLink uacQidLink : uacQidLinks) {
        List<Event> events = uacQidLink.getEvents();

        for (Event event : events) {
          caseEvents.add(this.mapperFacade.map(event, CaseEventDTO.class));
        }
      }
      if (caze.getEvents() != null) {
        for (Event event : caze.getEvents()) {
          caseEvents.add(this.mapperFacade.map(event, CaseEventDTO.class));
        }
      }
    }

    caseContainerDTO.setCaseEvents(caseEvents);

    return caseContainerDTO;
  }
}
