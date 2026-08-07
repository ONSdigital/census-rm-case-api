package uk.gov.ons.census.caseapisvc.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
    description = "Data Transfer Object representing an event with header and payload",
    example =
        "{\"header\":{\"version\":\"1.0\",\"topic\":\"rm-case-events\",\"source\":\"CASE_API\",\"channel\":\"INTERNAL\",\"dateTime\":\"2024-01-15T10:30:00Z\",\"messageId\":\"a11e3456-e89b-12d3-a456-426614174000\",\"correlationId\":\"a11e3456-e89b-12d3-a456-426614174001\",\"originatingUser\":\"system\"},\"payload\":{\"uac\":{\"uacHash\":\"abc123def456\",\"uac\":\"U123456\",\"active\":true,\"questionnaireId\":\"Q123456\",\"caseType\":\"HH\",\"region\":\"E12000009\",\"caseId\":\"a11e3456-e89b-12d3-a456-426614174000\",\"collectionExerciseId\":\"b22e3456-e89b-12d3-a456-426614174000\",\"formType\":\"H\",\"individualCaseId\":\"c33e3456-e89b-12d3-a456-426614\"}}}")
public class EventDTO {
  @Schema(description = "Event header containing metadata about the event")
  private EventHeaderDTO header;

  @Schema(description = "Event payload containing event-specific data")
  private PayloadDTO payload;
}
