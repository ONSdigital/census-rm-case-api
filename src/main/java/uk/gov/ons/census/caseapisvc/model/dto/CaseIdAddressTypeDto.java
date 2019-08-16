package uk.gov.ons.census.caseapisvc.model.dto;

import lombok.Data;

@Data
public class CaseIdAddressTypeDto {
  private String caseId;
  private String addressType;
}
