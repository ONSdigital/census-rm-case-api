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

  private static final String RM_UAC_CREATED = "RM_UAC_CREATED";

  private static final String ADDRESS_LEVEL_UNIT = "U";
  private static final String ADDRESS_LEVEL_ESTAB = "E";

  private static final String COUNTRY_CODE_ENGLAND = "E";
  private static final String COUNTRY_CODE_WALES = "W";
  private static final String COUNTRY_CODE_NORTHERN_IRELAND = "N";

  private static final String CASE_TYPE_HOUSEHOLD = "HH";
  private static final String CASE_TYPE_SPG = "SPG";
  private static final String CASE_TYPE_CE = "CE";

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
    return uacQidCreatedPayload;
  }

  public void sendUacQidCreatedEvent(UacQidCreatedPayloadDTO uacQidPayload) {
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
    if (!country.equals(COUNTRY_CODE_ENGLAND)
        && !country.equals(COUNTRY_CODE_WALES)
        && !country.equals(COUNTRY_CODE_NORTHERN_IRELAND)) {
      throw new IllegalArgumentException(
          String.format("Unknown Country for treatment code %s", treatmentCode));
    }

    if (individual
        && (isCeCaseType(treatmentCode)
            || isHouseholdCaseType(treatmentCode)
            || isSpgUnitLevelCase(treatmentCode, addressLevel))) {
      switch (country) {
        case COUNTRY_CODE_ENGLAND:
          return 21;
        case COUNTRY_CODE_WALES:
          return 22;
        case COUNTRY_CODE_NORTHERN_IRELAND:
          return 24;
      }
    } else if (isHouseholdCaseType(treatmentCode) || isSpgCaseType(treatmentCode)) {
      switch (country) {
        case COUNTRY_CODE_ENGLAND:
          return 1;
        case COUNTRY_CODE_WALES:
          return 2;
        case COUNTRY_CODE_NORTHERN_IRELAND:
          return 4;
      }
    } else if (isCE1RequestForEstabCeCase(treatmentCode, addressLevel, individual)) {
      switch (country) {
        case COUNTRY_CODE_ENGLAND:
          return 31;
        case COUNTRY_CODE_WALES:
          return 32;
        case COUNTRY_CODE_NORTHERN_IRELAND:
          return 34;
      }
    } else {
      throw new IllegalArgumentException(
          String.format(
              "Unexpected combination of Case Type, Address level and individual request. treatment code: '%s', address level: '%s', individual request: '%s'",
              treatmentCode, addressLevel, individual));
    }

    throw new RuntimeException(
        String.format(
            "Unprocessable combination of Case Type, Address level and individual request. treatment code: '%s', address level: '%s', individual request: '%s'",
            treatmentCode, addressLevel, individual));
  }

  private static boolean isCE1RequestForEstabCeCase(
      String treatmentCode, String addressLevel, boolean individual) {
    return isCeCaseType(treatmentCode) && addressLevel.equals(ADDRESS_LEVEL_ESTAB) && !individual;
  }

  private static boolean isSpgUnitLevelCase(String treatmentCode, String addressLevel) {
    return isSpgCaseType(treatmentCode) && addressLevel.equals(ADDRESS_LEVEL_UNIT);
  }

  private static boolean isSpgCaseType(String treatmentCode) {
    return treatmentCode.startsWith(CASE_TYPE_SPG);
  }

  private static boolean isHouseholdCaseType(String treatmentCode) {
    return treatmentCode.startsWith(CASE_TYPE_HOUSEHOLD);
  }

  private static boolean isCeCaseType(String treatmentCode) {
    return treatmentCode.startsWith(CASE_TYPE_CE);
  }
}
