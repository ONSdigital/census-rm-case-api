package uk.gov.ons.census.caseapisvc.model.dto;

import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class EventDTO {
  private String type;
  private String source = "RESPONSE_MANAGEMENT";
  private String channel = "RM";
  private OffsetDateTime dateTime;
  private String transactionId;
}
