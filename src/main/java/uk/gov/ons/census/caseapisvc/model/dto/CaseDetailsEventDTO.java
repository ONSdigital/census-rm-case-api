package uk.gov.ons.census.caseapisvc.model.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class CaseDetailsEventDTO {

  private UUID id;

  private String eventType;

  private String eventDescription;

  private OffsetDateTime eventDate;

  private String eventChannel = "RM";

  private UUID eventTransactionId;

  private OffsetDateTime rmEventProcessed;

  private String eventSource;

  private String eventPayload;

  private OffsetDateTime messageTimestamp;
}
