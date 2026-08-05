package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import uk.gov.ons.census.common.model.entity.RefusalType;

@Data
public class CaseDetailsDTO {

  @JsonProperty("id")
  private UUID caseId;

  private Long caseRef;

  private String uprn;

  private String estabUprn;

  private String caseType;

  private String addressType;

  private String estabType;

  private String addressLevel;

  private String abpCode;

  private String organisationName;

  private String addressLine1;

  private String addressLine2;

  private String addressLine3;

  private String townName;

  private String postcode;

  private String latitude;

  private String longitude;

  private String oa;

  private String lsoa;

  private String msoa;

  private String lad;

  private String region;

  private String htcWillingness;

  private String htcDigital;

  private String fieldCoordinatorId;

  private String fieldOfficerId;

  private String treatmentCode;

  private Integer ceExpectedCapacity;

  private int ceActualResponses;

  private UUID collectionExerciseId;

  private OffsetDateTime createdDateTime;

  List<CaseDetailsEventDTO> events;

  private boolean receiptReceived;

  private RefusalType refusalReceived;

  private boolean invalid;

  private OffsetDateTime lastUpdated;

  private String printBatch;

  private boolean surveyLaunched;
}
