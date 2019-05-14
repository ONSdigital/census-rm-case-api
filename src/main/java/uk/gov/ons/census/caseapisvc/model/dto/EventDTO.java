package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class EventDTO {

  private String id;

  private String category;

  @JsonProperty("createdDateTime")
  private String eventDate;

  @JsonProperty("description")
  private String eventDescription;
}
