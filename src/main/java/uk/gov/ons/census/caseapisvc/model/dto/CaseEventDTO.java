package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class CaseEventDTO {

  private UUID id;

  @JsonProperty("eventType")
  private EventTypeDTO type;

  private String description;

  @JsonProperty("createdDateTime")
  private OffsetDateTime dateTime;
}
