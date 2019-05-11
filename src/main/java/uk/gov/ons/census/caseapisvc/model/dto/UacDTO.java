package uk.gov.ons.census.caseapisvc.model.dto;

import lombok.Data;

@Data
public class UacDTO {
  private String uacHash;
  private String uac;
  private boolean active;
  private String questionnaireId;
  private String caseType;
  private String region;
  private String caseId;
  private String collectionExerciseId;
}
