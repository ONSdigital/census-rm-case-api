package uk.gov.ons.census.caseapisvc.model.dto;

import lombok.Data;

@Data
public class UacQidDTO {
  private String questionnaireId;
  private String uac;
}