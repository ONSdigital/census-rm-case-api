package uk.gov.ons.census.caseapisvc.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CaseDetailsDTO {

  private String questionnaireType;
  private UUID caseId;
}
