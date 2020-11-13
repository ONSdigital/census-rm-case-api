package uk.gov.ons.census.caseapisvc.model.entity;

import lombok.Data;
import uk.gov.ons.census.caseapisvc.model.dto.NonCompliancelTypeDTO;

@Data
public class CaseMetadata {

  private Boolean secureEstablishment;
  private String channel;
  private NonCompliancelTypeDTO nonCompliance;
}
