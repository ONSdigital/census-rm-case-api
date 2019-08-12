package uk.gov.ons.census.caseapisvc.model.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UacQidCreatedEventDTO {
  private final String type = "RM_UAC_CREATED";
  private final String source = "RESPONSE_MANAGEMENT";
  private final String channel = "RM";
  private OffsetDateTime dateTime;
  private String transactionId;
}
