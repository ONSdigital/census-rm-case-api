package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
@Schema(
    description = "Data Transfer Object representing a census case container",
    example =
        "{\"caseRef\":\"100000000000001\",\"id\":\"a11e3456-e89b-12d3-a456-426614174000\",\"uprn\":\"123456789\",\"postcode\":\"AB12CD\",\"addressType\":\"HOUSEHOLD\"}")
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

  @Schema(description = "Establishment type code")
  private String estabType;

  @Schema(description = "Unique Property Reference Number", example = "123456789")
  private String uprn;

  @Schema(description = "Establishment UPRN for non-household establishments")
  private String estabUprn;

  @Schema(description = "Collection Exercise UUID identifier")
  private UUID collectionExerciseId;

  @Schema(description = "Type of survey (e.g., CENSUS, OTHER_SURVEY)")
  private String surveyType;

  @Schema(description = "Type of address (e.g., HOUSEHOLD, COMMUNAL, OTHER)", example = "HOUSEHOLD")
  private String addressType;

  @Schema(description = "Case type classification (e.g., NEW, REPLACEMENT)")
  private String caseType;

  @Schema(description = "Date and time when the case was created", example = "2024-01-15T10:30:00Z")
  private OffsetDateTime createdDateTime;

  @Schema(description = "First line of street address")
  private String addressLine1;

  @Schema(description = "Second line of street address")
  private String addressLine2;

  @Schema(description = "Third line of street address")
  private String addressLine3;

  @Schema(description = "Town or city name")
  private String townName;

  @Schema(description = "UK postal code", example = "AB12CD")
  private String postcode;

  @Schema(description = "Name of organization for establishment addresses")
  private String organisationName;

  @Schema(description = "Address level classification")
  private String addressLevel;

  @Schema(description = "Address Based Priority code")
  private String abpCode;

  @Schema(description = "Administrative region code")
  private String region;

  @Schema(description = "Geographic latitude coordinate", example = "51.5074")
  private String latitude;

  @Schema(description = "Geographic longitude coordinate", example = "-0.1278")
  private String longitude;

  @Schema(description = "Output Area code")
  private String oa;

  @Schema(description = "Lower Layer Super Output Area code")
  private String lsoa;

  @Schema(
      description = "Date and time when the case was last updated",
      example = "2024-01-20T14:45:00Z")
  private OffsetDateTime lastUpdated;

  @Schema(description = "Middle Layer Super Output Area code")
  private String msoa;

  @Schema(description = "Local Authority District code")
  private String lad;

  @Schema(description = "List of events associated with the case")
  private List<CaseEventDTO> caseEvents;

  @Schema(description = "Indicator whether address is a secure establishment", example = "false")
  private Boolean secureEstablishment;

  @Schema(
      description = "Flag indicating if the case record is marked as invalid",
      example = "false")
  private boolean invalid;
}
