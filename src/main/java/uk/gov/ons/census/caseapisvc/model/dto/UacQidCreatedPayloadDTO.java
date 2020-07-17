package uk.gov.ons.census.caseapisvc.model.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class UacQidCreatedPayloadDTO {
  private String uac;
  private String qid;
  private UUID caseId;
}
