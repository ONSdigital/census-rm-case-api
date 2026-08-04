package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

@Data
@Schema(
    description = "Data Transfer Object representing an event associated with a case",
    example =
        "{\"id\":\"f47ac10b-58cc-4372-a567-0e02b2c3d479\",\"eventType\":\"NEW_CASE\",\"description\":\"Case created\",\"createdDateTime\":\"2024-01-15T10:30:00Z\"}")
public class CaseEventDTO {

  @Schema(
      description = "Unique event identifier (UUID)",
      example = "f47ac10b-58cc-4372-a567-0e02b2c3d479",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private UUID id;

  @JsonProperty("eventType")
  @Schema(
      description =
          "Type of event that occurred on the case (e.g., NEW_CASE, RECEIPT, REFUSAL, INVALID_CASE, EQ_LAUNCH)",
      example = "NEW_CASE",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private EventTypeDTO type;

  @Schema(
      description = "Human-readable description of the event",
      example = "Case created for collection exercise")
  private String description;

  @JsonProperty("createdDateTime")
  @Schema(
      description = "Date and time when the event was created",
      example = "2024-01-15T10:30:00Z",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private OffsetDateTime dateTime;
}
