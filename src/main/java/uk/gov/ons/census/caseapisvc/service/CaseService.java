package uk.gov.ons.census.caseapisvc.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.List;
import java.util.UUID;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.ons.census.caseapisvc.exception.CaseIdInvalidException;
import uk.gov.ons.census.caseapisvc.exception.CaseIdNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.CaseReferenceNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.QidNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.UPRNNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.UacQidLinkWithNoCaseException;
import uk.gov.ons.census.caseapisvc.model.dto.EventDTO;
import uk.gov.ons.census.caseapisvc.model.dto.FulfilmentRequestDTO;
import uk.gov.ons.census.caseapisvc.model.dto.PayloadDTO;
import uk.gov.ons.census.caseapisvc.model.dto.ResponseManagementEvent;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.model.entity.UacQidLink;
import uk.gov.ons.census.caseapisvc.model.repository.CaseRepository;
import uk.gov.ons.census.caseapisvc.model.repository.UacQidLinkRepository;

@Service
public class CaseService {
  private static final Logger log = LoggerFactory.getLogger(CaseService.class);

  private final static String RM_TELEPHONE_CAPTURE_HOUSEHOLD_INDIVIDUAL = "RM_TC_HI";
  private final static String FULFILMENT_REQUEST_EVENT_TYPE = "FULFILMENT_REQUESTED";

  private final CaseRepository caseRepo;
  private final UacQidLinkRepository uacQidLinkRepository;
  private RabbitTemplate rabbitTemplate;

  @Value("${queueconfig.events-exchange}")
  private String eventsExchange;

  @Value("${queueconfig.fulfilment-event-routing-key}")
  private String fulfilmentEventRoutingKey;

  @Autowired
  public CaseService(CaseRepository caseRepo, UacQidLinkRepository uacQidLinkRepository,
      RabbitTemplate rabbitTemplate) {
    this.caseRepo = caseRepo;
    this.uacQidLinkRepository = uacQidLinkRepository;
    this.rabbitTemplate = rabbitTemplate;
  }

  public List<Case> findByUPRN(String uprn) {
    log.debug("Entering findByUPRN");

    return caseRepo.findByUprn(uprn).orElseThrow(() -> new UPRNNotFoundException(uprn));
  }

  public Case findByCaseId(String caseId) {
    log.debug("Entering findByCaseId");

    UUID caseIdUUID = validateAndConvertCaseIdToUUID(caseId);

    return caseRepo
        .findByCaseId(caseIdUUID)
        .orElseThrow(() -> new CaseIdNotFoundException(caseIdUUID.toString()));
  }

  public Case findByReference(int reference) {
    log.debug("Entering findByReference");

    return caseRepo
        .findByCaseRef(reference)
        .orElseThrow(() -> new CaseReferenceNotFoundException(reference));
  }

  public Case findCaseByQid(String qid) {
    UacQidLink uacQidLink =
        uacQidLinkRepository.findByQid(qid).orElseThrow(() -> new QidNotFoundException(qid));

    if (uacQidLink.getCaze() == null) {
      throw new UacQidLinkWithNoCaseException(qid);
    }

    return uacQidLink.getCaze();
  }

  public boolean caseExistsByCaseId(String caseId) {
    UUID caseIdUUID = validateAndConvertCaseIdToUUID(caseId);
    return caseRepo.existsCaseByCaseId(caseIdUUID);
  }

  public UacQidLink findCCSUacQidLinkByCaseId(String caseId) {
    UUID caseIdUUID = validateAndConvertCaseIdToUUID(caseId);
    return uacQidLinkRepository
        .findOneByCcsCaseIsTrueAndCazeCaseIdAndCazeSurvey(caseIdUUID, "CCS")
        .orElseThrow(() -> new QidNotFoundException(caseIdUUID));
  }

  private UUID validateAndConvertCaseIdToUUID(String caseId) {
    UUID caseIdUUID;

    try {
      caseIdUUID = UUID.fromString(caseId);
    } catch (IllegalArgumentException iae) {
      throw new CaseIdInvalidException(caseId);
    }

    return caseIdUUID;
  }

  public void buildAndSendHiTelephoneCaptureFulfilmentRequest(String parentCaseId, String individualCaseId){
    FulfilmentRequestDTO fulfilmentRequestDTO = new FulfilmentRequestDTO();
    fulfilmentRequestDTO.setCaseId(parentCaseId);
    fulfilmentRequestDTO.setFulfilmentCode(RM_TELEPHONE_CAPTURE_HOUSEHOLD_INDIVIDUAL);
    fulfilmentRequestDTO.setIndividualCaseId(individualCaseId);

    PayloadDTO payloadDTO = new PayloadDTO();
    payloadDTO.setFulfilmentRequest(fulfilmentRequestDTO);

    EventDTO eventDTO = new EventDTO(FULFILMENT_REQUEST_EVENT_TYPE);

    ResponseManagementEvent responseManagementEvent = new ResponseManagementEvent(eventDTO, payloadDTO);

    log.with("caseId", parentCaseId)
        .with("individualCaseId", individualCaseId)
        .with("transactionId", eventDTO.getTransactionId())
        .debug("Sending UAC QID created event");

    rabbitTemplate.convertAndSend(eventsExchange, fulfilmentEventRoutingKey, responseManagementEvent);
  }
}
