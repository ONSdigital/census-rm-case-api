package uk.gov.ons.census.caseapisvc.service;

import static com.google.cloud.spring.pubsub.support.PubSubTopicUtils.toProjectTopicName;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.ons.census.caseapisvc.client.UacQidServiceClient;
import uk.gov.ons.census.caseapisvc.messaging.MessageSender;
import uk.gov.ons.census.caseapisvc.model.dto.EventDTO;
import uk.gov.ons.census.caseapisvc.model.dto.EventHeaderDTO;
import uk.gov.ons.census.caseapisvc.model.dto.NewQidLink;
import uk.gov.ons.census.caseapisvc.model.dto.PayloadDTO;
import uk.gov.ons.census.caseapisvc.model.dto.UacDTO;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedPayloadDTO;
import uk.gov.ons.census.caseapisvc.model.repository.UacQidLinkRepository;
import uk.gov.ons.census.common.model.entity.Case;
import uk.gov.ons.census.common.model.entity.UacQidLink;

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

  private final UacQidServiceClient uacQidServiceClient;
  private final UacQidLinkRepository uacQidLinkRepository;
  private final MessageSender messageSender;

  @Value("${queueconfig.questionnaire-linked-event-routing-key}")
  String questionnaireLinkedEventRoutingKey;

  @Value("${spring.cloud.gcp.pubsub.project-id}")
  String pubsubProject;

  @Autowired
  public UacQidService(
      UacQidServiceClient uacQidServiceClient,
      UacQidLinkRepository uacQidLinkRepository,
      MessageSender messageSender) {
    this.uacQidServiceClient = uacQidServiceClient;
    this.uacQidLinkRepository = uacQidLinkRepository;
    this.messageSender = messageSender;
  }

  public UacQidCreatedPayloadDTO createAndLinkUacQid(UUID caseId, int questionnaireType) {
    UacQidCreatedPayloadDTO uacQidCreatedPayload =
        uacQidServiceClient.generateUacQid(questionnaireType);
    uacQidCreatedPayload.setCaseId(caseId);
    return uacQidCreatedPayload;
  }

  public UacQidLink findUacQidLinkByQid(String qid) {
    return uacQidLinkRepository
        .findByQid(qid)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("QID  '%s' not found", qid)));
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
    uacDTO.setCaseId(caseToLink.getId());
    uacDTO.setQuestionnaireId(uacQidLink.getQid());

    EventDTO event = new EventDTO();
    EventHeaderDTO eventHeader = new EventHeaderDTO();
    eventHeader.setChannel(newQidLink.getChannel());
    eventHeader.setDateTime(OffsetDateTime.now());
    eventHeader.setTopic(QUESTIONNAIRE_LINKED_EVENT_TYPE);
    eventHeader.setMessageId(newQidLink.getTransactionId());

    PayloadDTO payloadDTO = new PayloadDTO();
    payloadDTO.setUac(uacDTO);

    event.setHeader(eventHeader);
    event.setPayload(payloadDTO);

    String topic = toProjectTopicName(questionnaireLinkedEventRoutingKey, pubsubProject).toString();
    messageSender.sendMessage(topic, event);
  }
}
