package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.OffsetDateTime;
import lombok.Data;
import uk.gov.ons.census.caseapisvc.serialization.CustomOffsetDateTimeSerializer;

@Data
public class EventDTO {

  private String id;

  private String eventType;

  @JsonProperty("description")
  private String eventDescription;

  @JsonProperty("createdDateTime")
  @JsonSerialize(using = CustomOffsetDateTimeSerializer.class)
  private OffsetDateTime eventDate;
}
