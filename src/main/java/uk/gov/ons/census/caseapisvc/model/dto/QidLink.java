package uk.gov.ons.census.caseapisvc.model.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class QidLink {
  String questionnaireId;
  UUID caseId;
}
