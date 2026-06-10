package uk.gov.ons.census.caseapisvc.model.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class UacLaunchDetails {
  private boolean active;
  private String collectionInstrumentUrl;
  private String qid;
  private UUID caseId;
}
