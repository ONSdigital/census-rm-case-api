package uk.gov.ons.census.caseapisvc.model.dto;

import lombok.Data;

@Data
public class TelephoneCaptureDTO {
  private String questionnaireId;
  private String uac;
  private String formType;
  private String questionnaireType;
}
