package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class CaseEventDTO {

  private String id;

  private String eventType;

  @JsonProperty("description")
  private String eventDescription;

  @JsonProperty("createdDateTime")
  private OffsetDateTime eventDate;
}
