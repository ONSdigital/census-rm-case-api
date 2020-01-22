package uk.gov.ons.census.caseapisvc.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.ons.census.caseapisvc.client.UacQidServiceClient;
import uk.gov.ons.census.caseapisvc.model.dto.EventDTO;
import uk.gov.ons.census.caseapisvc.model.dto.PayloadDTO;
import uk.gov.ons.census.caseapisvc.model.dto.ResponseManagementEvent;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedPayloadDTO;

@Service
public class UacQidService {

  private static final Logger log = LoggerFactory.getLogger(UacQidService.class);
  private final String RM_UAC_CREATED = "RM_UAC_CREATED";

  private RabbitTemplate rabbitTemplate;
  private UacQidServiceClient uacQidServiceClient;

  @Value("${queueconfig.uac-qid-created-exchange}")
  private String uacQidCreatedExchange;

  public UacQidService(RabbitTemplate rabbitTemplate, UacQidServiceClient uacQidServiceClient) {
    this.rabbitTemplate = rabbitTemplate;
    this.uacQidServiceClient = uacQidServiceClient;
  }

  public UacQidCreatedPayloadDTO createAndLinkUacQid(String caseId, int questionnaireType) {
    UacQidCreatedPayloadDTO uacQidCreatedPayload =
        uacQidServiceClient.generateUacQid(questionnaireType);
    uacQidCreatedPayload.setCaseId(caseId);
    sendUacQidCreatedEvent(uacQidCreatedPayload);
    return uacQidCreatedPayload;
  }

  private void sendUacQidCreatedEvent(UacQidCreatedPayloadDTO uacQidPayload) {
    EventDTO eventDTO = buildUacQidCreatedEventDTO();
    ResponseManagementEvent responseManagementEvent =
        buildUacQidCreatedDTO(eventDTO, uacQidPayload);
    log.with("caseId", uacQidPayload.getCaseId())
        .with("transactionId", eventDTO.getTransactionId())
        .debug("Sending UAC QID created event");
    rabbitTemplate.convertAndSend(uacQidCreatedExchange, "", responseManagementEvent);
  }

  private EventDTO buildUacQidCreatedEventDTO() {
    EventDTO eventDTO = new EventDTO();
    eventDTO.setType(RM_UAC_CREATED);
    eventDTO.setDateTime(OffsetDateTime.now());
    eventDTO.setTransactionId(UUID.randomUUID().toString());
    return eventDTO;
  }

  private ResponseManagementEvent buildUacQidCreatedDTO(
      EventDTO eventDTO, UacQidCreatedPayloadDTO uacQidCreatedPayloadDTO) {
    ResponseManagementEvent responseManagementEvent = new ResponseManagementEvent();
    responseManagementEvent.setEvent(eventDTO);
    PayloadDTO payloadDTO = new PayloadDTO();
    payloadDTO.setUacQidCreated(uacQidCreatedPayloadDTO);
    responseManagementEvent.setPayload(payloadDTO);
    return responseManagementEvent;
  }

  public static int calculateQuestionnaireType(String treatmentCode, String addressLevel) {
    return calculateQuestionnaireType(treatmentCode, addressLevel, false);
  }

  public static int calculateQuestionnaireType(
      String treatmentCode, String addressLevel, boolean individual) {
    String country = treatmentCode.substring(treatmentCode.length() - 1);
    if (!country.equals("E") && !country.equals("W") && !country.equals("N")) {
      throw new IllegalArgumentException(
          String.format("Unknown Country for treatment code %s", treatmentCode));
    }

    if (isUnitLevelCE(treatmentCode, addressLevel)
        || isIndividualRequestForHouseholdCaseType(treatmentCode, individual)) {
      switch (country) {
        case "E":
          return 21;
        case "W":
          return 22;
        case "N":
          return 24;
      }
    } else if (isHouseholdCaseType(treatmentCode) || isSpgCaseType(treatmentCode)) {
      switch (country) {
        case "E":
          return 1;
        case "W":
          return 2;
        case "N":
          return 4;
      }
    } else {
      throw new IllegalArgumentException(
          String.format(
              "Unexpected Case Type or Address level for treatment code: '%s', address level: '%s'",
              treatmentCode, addressLevel));
    }

    throw new RuntimeException(
        String.format(
            "Unprocessable treatment code: '%s' or address level: '%s'",
            treatmentCode, addressLevel));
  }

  private static boolean isSpgCaseType(String treatmentCode) {
    return treatmentCode.startsWith("SPG");
  }

  private static boolean isHouseholdCaseType(String treatmentCode) {
    return treatmentCode.startsWith("HH");
  }

  private static boolean isIndividualRequestForHouseholdCaseType(
      String treatmentCode, boolean individual) {
    return isHouseholdCaseType(treatmentCode) && individual;
  }

  private static boolean isUnitLevelCE(String treatmentCode, String addressLevel) {
    return treatmentCode.startsWith("CE") && addressLevel.equals("U");
  }
}
