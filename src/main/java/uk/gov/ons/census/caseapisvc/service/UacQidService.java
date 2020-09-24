package uk.gov.ons.census.caseapisvc.service;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.ons.census.caseapisvc.client.UacQidServiceClient;
import uk.gov.ons.census.caseapisvc.exception.QidNotFoundException;
import uk.gov.ons.census.caseapisvc.model.dto.*;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.model.entity.UacQidLink;
import uk.gov.ons.census.caseapisvc.model.repository.UacQidLinkRepository;

@Service
public class UacQidService {
  private static final String ADDRESS_LEVEL_ESTAB = "E";

  private static final String COUNTRY_CODE_ENGLAND = "E";
  private static final String COUNTRY_CODE_WALES = "W";
  private static final String COUNTRY_CODE_NORTHERN_IRELAND = "N";

  private static final String CASE_TYPE_HOUSEHOLD = "HH";
  private static final String CASE_TYPE_SPG = "SPG";
  private static final String CASE_TYPE_CE = "CE";
  private static final String QUESTIONNAIRE_LINKED_EVENT_TYPE = "QUESTIONNAIRE_LINKED";

  private UacQidServiceClient uacQidServiceClient;
  private UacQidLinkRepository uacQidLinkRepository;
  private RabbitTemplate rabbitTemplate;

  @Value("${queueconfig.events-exchange}")
  private String eventsExchange;

  @Value("${queueconfig.questionnaire-linked-event-routing-key}")
  private String questionnaireLinkedEventRoutingKey;

  @Autowired
  public UacQidService(
      UacQidServiceClient uacQidServiceClient,
      RabbitTemplate rabbitTemplate,
      UacQidLinkRepository uacQidLinkRepository) {
    this.uacQidServiceClient = uacQidServiceClient;
    this.rabbitTemplate = rabbitTemplate;
    this.uacQidLinkRepository = uacQidLinkRepository;
  }

  public UacQidCreatedPayloadDTO createAndLinkUacQid(UUID caseId, int questionnaireType) {
    UacQidCreatedPayloadDTO uacQidCreatedPayload =
        uacQidServiceClient.generateUacQid(questionnaireType);
    uacQidCreatedPayload.setCaseId(caseId);
    return uacQidCreatedPayload;
  }

  public UacQidLink findUacQidLinkByQid(String qid) {
    return uacQidLinkRepository.findByQid(qid).orElseThrow(() -> new QidNotFoundException(qid));
  }

  public static int calculateQuestionnaireType(
      String caseType, String region, String addressLevel, String surveyType) {
    return calculateQuestionnaireType(caseType, region, addressLevel, surveyType, false);
  }

  public static int calculateQuestionnaireType(
      String caseType, String region, String addressLevel, String surveyType, boolean individual) {

    if (surveyType.equals("CCS")) {
      return 71;
    }

    String country = region.substring(0, 1);
    if (!country.equals(COUNTRY_CODE_ENGLAND)
        && !country.equals(COUNTRY_CODE_WALES)
        && !country.equals(COUNTRY_CODE_NORTHERN_IRELAND)) {
      throw new IllegalArgumentException(
          String.format("Unknown Country for treatment code %s", caseType));
    }

    if (individual) {
      switch (country) {
        case COUNTRY_CODE_ENGLAND:
          return 21;
        case COUNTRY_CODE_WALES:
          return 22;
        case COUNTRY_CODE_NORTHERN_IRELAND:
          return 24;
      }
    } else if (isHouseholdCaseType(caseType) || isSpgCaseType(caseType)) {
      switch (country) {
        case COUNTRY_CODE_ENGLAND:
          return 1;
        case COUNTRY_CODE_WALES:
          return 2;
        case COUNTRY_CODE_NORTHERN_IRELAND:
          return 4;
      }
    } else if (isCE1RequestForEstabCeCase(caseType, addressLevel, individual)) {
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
              caseType, addressLevel, individual));
    }

    throw new RuntimeException(
        String.format(
            "Unprocessable combination of Case Type, Address level and individual request. treatment code: '%s', address level: '%s', individual request: '%s'",
            caseType, addressLevel, individual));
  }

  private static boolean isCE1RequestForEstabCeCase(
      String treatmentCode, String addressLevel, boolean individual) {
    return isCeCaseType(treatmentCode) && addressLevel.equals(ADDRESS_LEVEL_ESTAB) && !individual;
  }

  private static boolean isSpgCaseType(String caseType) {
    return caseType.equals(CASE_TYPE_SPG);
  }

  private static boolean isHouseholdCaseType(String caseType) {
    return caseType.equals(CASE_TYPE_HOUSEHOLD);
  }

  private static boolean isCeCaseType(String caseType) {
    return caseType.equals(CASE_TYPE_CE);
  }

  public void buildAndSendQuestionnaireLinkedEvent(
      UacQidLink uacQidLink, Case caseToLink, NewQidLink newQidLink) {
    UacDTO uacDTO = new UacDTO();
    uacDTO.setCaseId(caseToLink.getCaseId());
    uacDTO.setQuestionnaireId(uacQidLink.getQid());

    EventDTO eventDTO = new EventDTO();
    eventDTO.setType(QUESTIONNAIRE_LINKED_EVENT_TYPE);
    eventDTO.setDateTime(OffsetDateTime.now());
    eventDTO.setTransactionId(newQidLink.getTransactionId());
    eventDTO.setChannel(newQidLink.getChannel());

    PayloadDTO payloadDTO = new PayloadDTO();
    payloadDTO.setUac(uacDTO);

    ResponseManagementEvent responseManagementEvent =
        new ResponseManagementEvent(eventDTO, payloadDTO);

    rabbitTemplate.convertAndSend(
        eventsExchange, questionnaireLinkedEventRoutingKey, responseManagementEvent);
  }
}
