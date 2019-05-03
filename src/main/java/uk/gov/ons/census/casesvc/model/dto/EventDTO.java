package uk.gov.ons.census.casesvc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.UUID;
import lombok.Data;

@Data
public class EventDTO {

  private UUID id;

  private String category;

  @JsonProperty("createdDateTime")
  private Date eventDate;

  @JsonProperty("description")
  private String eventDescription;
}
