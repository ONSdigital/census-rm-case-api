package uk.gov.ons.census.caseapisvc.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.ons.census.caseapisvc.client.UacQidServiceClient;
import uk.gov.ons.census.caseapisvc.model.dto.PayloadDTO;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedDTO;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedEventDTO;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedPayloadDTO;

@Service
public class UacQidService {
  private static final Logger log = LoggerFactory.getLogger(UacQidService.class);

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
    UacQidCreatedEventDTO uacQidCreatedEventDTO = buildUacQidCreatedEventDTO();
    UacQidCreatedDTO uacQidCreatedDTO = buildUacQidCreatedDTO(uacQidCreatedEventDTO, uacQidPayload);
    log.with("caseId", uacQidPayload.getCaseId())
        .with("transactionId", uacQidCreatedEventDTO.getTransactionId())
        .debug("Sending UAC QID created event");
    rabbitTemplate.convertAndSend(uacQidCreatedExchange, "", uacQidCreatedDTO);
  }

  private UacQidCreatedEventDTO buildUacQidCreatedEventDTO() {
    UacQidCreatedEventDTO uacQidCreatedEventDTO = new UacQidCreatedEventDTO();
    uacQidCreatedEventDTO.setDateTime(OffsetDateTime.now());
    uacQidCreatedEventDTO.setTransactionId(UUID.randomUUID().toString());
    return uacQidCreatedEventDTO;
  }

  private UacQidCreatedDTO buildUacQidCreatedDTO(
      UacQidCreatedEventDTO uacQidCreatedEventDTO,
      UacQidCreatedPayloadDTO uacQidCreatedPayloadDTO) {
    UacQidCreatedDTO uacQidCreatedDTO = new UacQidCreatedDTO();
    uacQidCreatedDTO.setEvent(uacQidCreatedEventDTO);
    PayloadDTO payloadDTO = new PayloadDTO();
    payloadDTO.setUacQidCreated(uacQidCreatedPayloadDTO);
    uacQidCreatedDTO.setPayload(payloadDTO);
    return uacQidCreatedDTO;
  }

  public static int calculateQuestionnaireType(String treatmentCode, String addressLevel) {
    String country = treatmentCode.substring(treatmentCode.length() - 1);
    if (!country.equals("E") && !country.equals("W") && !country.equals("N")) {
      throw new IllegalArgumentException(
          String.format("Unknown Country for treatment code %s", treatmentCode));
    }

    if (treatmentCode.startsWith("HH")) {
      switch (country) {
        case "E":
          return 1;
        case "W":
          return 2;
        case "N":
          return 4;
      }
    } else if (treatmentCode.startsWith("CE") && addressLevel.equals("U")) {
      switch (country) {
        case "E":
          return 21;
        case "W":
          return 22;
        case "N":
          return 24;
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
}
