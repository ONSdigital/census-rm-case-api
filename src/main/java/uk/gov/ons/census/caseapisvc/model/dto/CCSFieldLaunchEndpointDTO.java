package uk.gov.ons.census.caseapisvc.model.dto;

import lombok.Data;

@Data
public class CCSFieldLaunchEndpointDTO {
  private String questionnaireId;
  private boolean active;
  private String formType;
}
