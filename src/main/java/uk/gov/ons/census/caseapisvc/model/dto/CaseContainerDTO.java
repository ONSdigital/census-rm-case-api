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
        "{\"caseRef\":\"100000000000001\",\"id\":\"a11e3456-e89b-12d3-a456-426614174000\",\"uprn\":\"123456789\",\"postcode\":\"AB12CD\",\"addressType\":\"HH\"}")
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

  @Schema(
      description =
          "Establishment type (e.g., HALL OF RESIDENCE, HOUSEHOLD, SHELTERED ACCOMMODATION, RESIDENTIAL CARAVAN, RESIDENTIAL BOAT)",
      example = "HOUSEHOLD")
  private String estabType;

  @Schema(description = "Unique Property Reference Number", example = "10008677190")
  private String uprn;

  @Schema(
      description = "Establishment UPRN for non-household establishments",
      example = "10008677190")
  private String estabUprn;

  @Schema(
      description = "Collection Exercise UUID identifier",
      example = "b22e3456-e89b-12d3-a456-426614174000")
  private UUID collectionExerciseId;

  @Schema(description = "Type of survey (e.g., CENSUS, CCS)", example = "CENSUS")
  private String surveyType;

  @Schema(description = "Residential address frame type (e.g., HH, CE)", example = "HH")
  private String addressType;

  @Schema(
      description =
          "Case type classification (e.g., HH, HI, CE). It will match addressType unless it is an individual (HI) case.",
      example = "HH")
  private String caseType;

  @Schema(description = "Date and time when the case was created", example = "2024-01-15T10:30:00Z")
  private OffsetDateTime createdDateTime;

  @Schema(description = "First address line", example = "Flat 51 Francombe House")
  private String addressLine1;

  @Schema(description = "Second address line", example = "Commercial Road")
  private String addressLine2;

  @Schema(description = "Third address line")
  private String addressLine3;

  @Schema(description = "Town or city name", example = "Windleybury")
  private String townName;

  @Schema(description = "UK postal code", example = "XX1 0XX")
  private String postcode;

  @Schema(description = "Name of the organisation at the address", example = "Acme Corporation")
  private String organisationName;

  @Schema(
      description = "Address level classification (e.g., E, U (signifying Establishment and Unit))",
      example = "U")
  private String addressLevel;

  @Schema(description = "AddressBase classification code", example = "RD06")
  private String abpCode;

  @Schema(description = "Administrative region code")
  private String region;

  @Schema(description = "Geographic latitude coordinate", example = "51.5074")
  private String latitude;

  @Schema(description = "Geographic longitude coordinate", example = "-0.1278")
  private String longitude;

  @Schema(description = "Output Area grid reference", example = "E00073438")
  private String oa;

  @Schema(description = "Lower Layer Super Output Area grid reference", example = "E01014540")
  private String lsoa;

  @Schema(
      description = "Date and time when the case was last updated",
      example = "2024-01-20T14:45:00Z")
  private OffsetDateTime lastUpdated;

  @Schema(description = "Middle Layer Super Output Area grid reference", example = "E02003043")
  private String msoa;

  @Schema(description = "Local Authority District code", example = "E06000023")
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
