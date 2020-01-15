package uk.gov.ons.census.caseapisvc.model.dto;

import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class EventDTO {
  private final String type;
  private final String source = "RESPONSE_MANAGEMENT";
  private final String channel = "RM";
  private OffsetDateTime dateTime;
  private String transactionId;

  public EventDTO(String type) {
    this.type = type;
  }
}
