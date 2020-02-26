package uk.gov.ons.census.caseapisvc.model.dto;

import lombok.Data;

@Data
public class CCSLaunchDTO {
  private String questionnaireId;
  private boolean active;
  private String formType;
}
