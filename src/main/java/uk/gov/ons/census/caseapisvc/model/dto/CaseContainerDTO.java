package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
@Schema(description = "Data Transfer Object representing a census case container")
public class CaseContainerDTO {
  @Schema(
      description = "Unique numeric reference for the case",
      example = "100000000000001",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String caseRef;

  @JsonProperty("id")
  @Schema(
      description = "Unique case UUID identifier",
      example = "a11e3456-e89b-12d3-a456-426614174000",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private UUID caseId;

  private String estabType;

  private String uprn;

  private String estabUprn;

  private UUID collectionExerciseId;

  private String surveyType;

  private String addressType;

  private String caseType;

  private OffsetDateTime createdDateTime;

  private String addressLine1;

  private String addressLine2;

  private String addressLine3;

  private String townName;

  private String postcode;

  private String organisationName;

  private String addressLevel;

  private String abpCode;

  private String region;

  private String latitude;

  private String longitude;

  private String oa;

  private String lsoa;

  private OffsetDateTime lastUpdated;

  private String msoa;

  private String lad;

  private List<CaseEventDTO> caseEvents;

  private Boolean secureEstablishment;

  private boolean invalid;
}
