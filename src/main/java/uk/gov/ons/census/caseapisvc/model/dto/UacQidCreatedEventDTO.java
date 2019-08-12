package uk.gov.ons.census.caseapisvc.model.dto;

import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class UacQidCreatedEventDTO {
  private final String type = "RM_UAC_CREATED";
  private final String source = "RESPONSE_MANAGEMENT";
  private final String channel = "RM";
  private OffsetDateTime dateTime;
  private String transactionId;
}
