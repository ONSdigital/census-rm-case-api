package uk.gov.ons.census.caseapisvc.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class QidLink {
  String questionnaireId;
  UUID caseId;
}
