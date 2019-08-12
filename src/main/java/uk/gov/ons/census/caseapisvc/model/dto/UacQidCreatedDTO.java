package uk.gov.ons.census.caseapisvc.model.dto;

import lombok.Data;

@Data
public class UacQidCreatedDTO {
  private UacQidCreatedEventDTO event;
  private PayloadDTO payload;
}
