package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EventDTO {

  private String id;

  private String eventType;

  @JsonProperty("description")
  private String eventDescription;

  @JsonProperty("createdDateTime")
  private String eventDate;
}
