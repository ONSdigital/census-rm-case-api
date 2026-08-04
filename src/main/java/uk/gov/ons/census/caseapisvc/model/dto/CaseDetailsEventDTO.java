package uk.gov.ons.census.caseapisvc.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

@Data
@Schema(
    description =
        "Detailed Data Transfer Object containing event attributes for case details response",
    example =
        "{\"id\":\"f47ac10b-58cc-4372-a567-0e02b2c3d479\",\"eventType\":\"NEW_CASE\",\"eventDescription\":\"Case created\",\"eventDate\":\"2024-01-15T10:30:00Z\",\"eventChannel\":\"RM\"}")
public class CaseDetailsEventDTO {

  @Schema(
      description = "Unique event identifier (UUID)",
      example = "f47ac10b-58cc-4372-a567-0e02b2c3d479",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private UUID id;

  @Schema(
      description =
          "Type of event (e.g., NEW_CASE, RECEIPT, REFUSAL, EQ_LAUNCH, UAC_AUTHENTICATION)",
      example = "NEW_CASE",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String eventType;

  @Schema(
      description = "Human-readable description of the event",
      example = "Case created for collection exercise")
  private String eventDescription;

  @Schema(
      description = "Date and time when the event occurred",
      example = "2024-01-15T10:30:00Z",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private OffsetDateTime eventDate;

  @Schema(
      description = "Channel through which the event was triggered (RM, CC, etc.)",
      example = "RM",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String eventChannel = "RM";

  @Schema(description = "Unique transaction identifier for this event")
  private UUID eventTransactionId;

  @Schema(
      description = "Date and time when the event was processed by RM",
      example = "2024-01-15T10:35:00Z")
  private OffsetDateTime rmEventProcessed;

  @Schema(description = "Source system that generated the event (e.g., UAC_SERVICE, PRINT_SERVICE)")
  private String eventSource;

  @Schema(description = "JSON payload containing event-specific data")
  private String eventPayload;

  @Schema(
      description = "Message timestamp from the originating system",
      example = "2024-01-15T10:30:00Z")
  private OffsetDateTime messageTimestamp;
}
