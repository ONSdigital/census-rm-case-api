package uk.gov.ons.census.caseapisvc.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedDTO;

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

  public void sendUacQidCreatedEvent(UacQidCreatedDTO uacQid, UUID caseId) {
    UacQidCreatedDTO uacQidCreatedDTO = buildUacQidCreatedDTO(uacQid, caseId);
    log.with("caseId", caseId).debug("Sending UAC QID created event");
    rabbitTemplate.convertAndSend(uacQidCreatedExchange, "", uacQidCreatedDTO);
  }

  private UacQidCreatedDTO buildUacQidCreatedDTO(UacQidCreatedDTO uacQid, UUID caseId) {
    UacQidCreatedDTO uacQidCreatedDTO = new UacQidCreatedDTO();
    uacQidCreatedDTO.setCaseId(caseId.toString());
    uacQidCreatedDTO.setUac(uacQid.getUac());
    uacQidCreatedDTO.setQid(uacQid.getQid());
    return uacQidCreatedDTO;
  }
}
