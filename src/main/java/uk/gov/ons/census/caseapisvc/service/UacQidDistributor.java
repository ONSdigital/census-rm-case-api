package uk.gov.ons.census.caseapisvc.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.ons.census.caseapisvc.model.dto.PayloadDTO;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedDTO;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedEventDTO;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedPayloadDTO;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class UacQidDistributor {
  private static final Logger log = LoggerFactory.getLogger(UacQidDistributor.class);

  private RabbitTemplate rabbitTemplate;

  @Value("${queueconfig.uac-qid-created-exchange}")
  private String uacQidCreatedExchange;

  public UacQidDistributor(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void sendUacQidCreatedEvent(UacQidCreatedPayloadDTO uacQidPayload) {
    UacQidCreatedEventDTO uacQidCreatedEventDTO = buildUacQidCreatedEventDTO();
    UacQidCreatedDTO uacQidCreatedDTO =
        buildUacQidCreatedDTO(uacQidCreatedEventDTO, uacQidPayload);
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
}
