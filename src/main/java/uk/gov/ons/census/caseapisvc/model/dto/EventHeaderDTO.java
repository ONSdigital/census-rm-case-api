package uk.gov.ons.census.caseapisvc.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

@Data
@Schema(
    description = "Data Transfer Object containing metadata header for an event message",
    example =
        "{\"version\":\"1.0\",\"topic\":\"rm-case-events\",\"source\":\"CASE_API\",\"channel\":\"INTERNAL\",\"dateTime\":\"2024-01-15T10:30:00Z\",\"messageId\":\"a11e3456-e89b-12d3-a456-426614174000\",\"correlationId\":\"a11e3456-e89b-12d3-a456-426614174001\",\"originatingUser\":\"system\"}")
public class EventHeaderDTO {
  @Schema(
      description = "Version of the event message schema",
      example = "1.0",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String version;

  @Schema(
      description = "Topic or category of the event message",
      example = "rm-case-events",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String topic;

  @Schema(
      description = "Source system that originated this event",
      example = "CASE_API",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String source;

  @Schema(
      description = "Channel through which the event was transmitted",
      example = "INTERNAL",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String channel;

  @Schema(
      description = "Date and time when the event message was created",
      example = "2024-01-15T10:30:00Z",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private OffsetDateTime dateTime;

  @Schema(
      description = "Unique message identifier for tracking individual event messages",
      example = "a11e3456-e89b-12d3-a456-426614174000",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private UUID messageId;

  @Schema(
      description = "Correlation ID for linking related events and messages",
      example = "a11e3456-e89b-12d3-a456-426614174001",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private UUID correlationId;

  @Schema(description = "User or service that originated the event", example = "system")
  private String originatingUser;
}
