package uk.gov.ons.census.caseapisvc.model.dto;

import lombok.Data;

@Data
public class UacQidCreatedDTO {
  private String uac;
  private String qid;
  private String caseId;
}
