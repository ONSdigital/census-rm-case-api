package uk.gov.ons.census.caseapisvc.model.entity;

import lombok.Data;

@Data
public class CaseMetadata {

  private Boolean secureEstablishment;
  private String channel;
}
