package uk.gov.ons.census.caseapisvc.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
  private EventHeaderDTO header;
  private PayloadDTO payload;
}
