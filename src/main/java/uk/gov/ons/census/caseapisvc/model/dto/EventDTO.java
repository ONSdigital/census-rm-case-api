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
        "{\"header\":{\"version\":\"1.0\",\"topic\":\"rm-event\",\"source\":\"RM\",\"channel\":\"INTERNAL\"},\"payload\":{\"uac\":{\"uacHash\":\"abc123\",\"qid\":\"Q123456\"}}}")
public class EventDTO {
  @Schema(description = "Event header containing metadata about the event")
  private EventHeaderDTO header;

  @Schema(description = "Event payload containing event-specific data")
  private PayloadDTO payload;
}
