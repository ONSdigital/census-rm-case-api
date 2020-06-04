package uk.gov.ons.census.caseapisvc.endpoint;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static uk.gov.ons.census.caseapisvc.service.UacQidService.calculateQuestionnaireType;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import io.micrometer.core.annotation.Timed;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
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
import uk.gov.ons.census.caseapisvc.model.dto.CCSLaunchDTO;
import uk.gov.ons.census.caseapisvc.model.dto.CaseContainerDTO;
import uk.gov.ons.census.caseapisvc.model.dto.CaseEventDTO;
import uk.gov.ons.census.caseapisvc.model.dto.TelephoneCaptureDTO;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedPayloadDTO;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.model.entity.Event;
import uk.gov.ons.census.caseapisvc.model.entity.UacQidLink;
import uk.gov.ons.census.caseapisvc.service.CaseService;
import uk.gov.ons.census.caseapisvc.service.UacQidService;
import uk.gov.ons.census.caseapisvc.validation.RequestValidator;

@RestController
@RequestMapping(value = "/cases")
@Timed
public final class CaseEndpoint {
  private static final Logger log = LoggerFactory.getLogger(CaseEndpoint.class);
  private static final String RM_TELEPHONE_CAPTURE_HOUSEHOLD_INDIVIDUAL = "RM_TC_HI";
  private static final String RM_TELEPHONE_CAPTURE = "RM_TC";
  private static final String HH_FORM_TYPE = "H";
  private static final String IND_FORM_TYPE = "I";
  private static final String CE1_FORM_TYPE = "C";

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
          boolean caseEvents,
      @RequestParam(value = "validAddressOnly", required = false, defaultValue = "false")
          boolean validAddressOnly) {
    log.debug("Entering findByUPRN");

    List<CaseContainerDTO> caseContainerDTOs = new LinkedList<>();

    for (Case caze : caseService.findByUPRN(uprn, validAddressOnly)) {
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
      @PathVariable("reference") long reference,
      @RequestParam(value = "caseEvents", required = false, defaultValue = "false")
          boolean caseEvents) {
    log.debug("Entering findByReference");

    return buildCaseContainerDTO(caseService.findByReference(reference), caseEvents);
  }

  @GetMapping(value = "/ccs/postcode/{postcode}")
  public List<CaseContainerDTO> findCCSCasesByPostcode(
      @PathVariable("postcode") String postcode,
      @RequestParam(value = "caseEvents", required = false, defaultValue = "false")
          boolean caseEvents) {
    log.debug("Entering findByPostcode");
    List<Case> cases = caseService.findCCSCasesByPostcode(postcode);
    return cases.stream()
        .map(c -> buildCaseContainerDTO(c, caseEvents))
        .collect(Collectors.toList());
  }

  @GetMapping(value = "/ccs/{caseId}/qid")
  public CCSLaunchDTO findCCSQidByCaseId(@PathVariable("caseId") String caseId) {
    log.debug("Entering findByCaseId");
    UacQidLink ccsUacQidLink = caseService.findCCSUacQidLinkByCaseId(caseId);

    CCSLaunchDTO ccsLaunchDTO = new CCSLaunchDTO();
    ccsLaunchDTO.setQuestionnaireId(ccsUacQidLink.getQid());
    ccsLaunchDTO.setActive(ccsUacQidLink.isActive());
    ccsLaunchDTO.setFormType(mapCCSQuestionnaireTypeToFormType(ccsUacQidLink.getQid()));
    return ccsLaunchDTO;
  }

  @GetMapping(value = "/{caseId}/qid")
  public TelephoneCaptureDTO getNewQidForTelephoneCapture(
      @PathVariable("caseId") String caseId,
      @RequestParam(value = "individual", required = false, defaultValue = "false")
          boolean individual,
      @RequestParam(value = "individualCaseId", required = false) String individualCaseId) {
    log.debug("Entering getNewQidByCaseId");

    Case caze = caseService.findByCaseId(caseId);
    RequestValidator.validateGetNewQidByCaseIdRequest(caze, individual, individualCaseId);

    if (individualCaseId != null && individual) {
      return handleNewIndividualTelephoneCaptureRequest(caze, individualCaseId);
    }

    return handleTelephoneCaptureRequest(caze, individual);
  }

  private TelephoneCaptureDTO handleTelephoneCaptureRequest(Case caze, boolean individual) {

    int questionnaireType =
        calculateQuestionnaireType(
            caze.getCaseType(), caze.getRegion(), caze.getAddressLevel(), individual);

    UacQidCreatedPayloadDTO uacQidCreatedPayload =
        uacQidService.createAndLinkUacQid(caze.getCaseId().toString(), questionnaireType);

    caseService.buildAndSendTelephoneCaptureFulfilmentRequest(
        caze.getCaseId().toString(), RM_TELEPHONE_CAPTURE, null, uacQidCreatedPayload);

    return buildTelephoneCaptureDTO(uacQidCreatedPayload, questionnaireType);
  }

  private TelephoneCaptureDTO handleNewIndividualTelephoneCaptureRequest(
      Case caze, String individualCaseId) {
    if (caseService.caseExistsByCaseId(individualCaseId)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          String.format("IndividualCaseId %s already exists", individualCaseId));
    }

    int questionnaireType =
        calculateQuestionnaireType(
            caze.getCaseType(), caze.getRegion(), caze.getAddressLevel(), true);

    UacQidCreatedPayloadDTO uacQidCreatedPayload =
        uacQidService.createAndLinkUacQid(individualCaseId, questionnaireType);

    caseService.buildAndSendTelephoneCaptureFulfilmentRequest(
        caze.getCaseId().toString(),
        RM_TELEPHONE_CAPTURE_HOUSEHOLD_INDIVIDUAL,
        individualCaseId,
        uacQidCreatedPayload);

    return buildTelephoneCaptureDTO(uacQidCreatedPayload, questionnaireType);
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

    if (caze.getMetadata() != null) {
      caseContainerDTO.setSecureEstablishment(caze.getMetadata().getSecureEstablishment());
    }

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

  private TelephoneCaptureDTO buildTelephoneCaptureDTO(
      UacQidCreatedPayloadDTO uacQidCreatedPayload, int questionnaireType) {
    TelephoneCaptureDTO telephoneCaptureDTO = new TelephoneCaptureDTO();
    telephoneCaptureDTO.setQuestionnaireId(uacQidCreatedPayload.getQid());
    telephoneCaptureDTO.setUac(uacQidCreatedPayload.getUac());
    telephoneCaptureDTO.setFormType(mapQuestionnaireTypeToFormType(questionnaireType));
    telephoneCaptureDTO.setQuestionnaireType(String.format("%02d", questionnaireType));
    return telephoneCaptureDTO;
  }

  private String mapQuestionnaireTypeToFormType(int questionnaireType) {
    switch (questionnaireType) {
      case 1:
      case 2:
      case 4:
        return HH_FORM_TYPE;
      case 21:
      case 22:
      case 24:
        return IND_FORM_TYPE;
      case 31:
      case 32:
      case 34:
        return CE1_FORM_TYPE;
      default:
        throw new IllegalArgumentException(
            String.format("Invalid QuestionnaireType: '%d'", questionnaireType));
    }
  }

  private String mapCCSQuestionnaireTypeToFormType(String qid) {
    int questionnaireType = Integer.parseInt(qid.substring(0, 2));

    switch (questionnaireType) {
      case 51:
      case 53:
      case 71:
      case 73:
        return HH_FORM_TYPE;
      case 81:
      case 83:
        return CE1_FORM_TYPE;
      default:
        return null;
    }
  }
}
