package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import uk.gov.ons.census.common.model.entity.RefusalType;

@Data
@Schema(
    description = "Comprehensive Data Transfer Object containing detailed case attributes",
    example =
        "{\"id\":\"a11e3456-e89b-12d3-a456-426614174000\",\"caseRef\":100000000000001,\"uprn\":\"123456789\",\"postcode\":\"AB12CD\",\"caseType\":\"NEW\"}")
public class CaseDetailsDTO {

  @JsonProperty("id")
  @Schema(
      description = "Unique case UUID identifier",
      example = "a11e3456-e89b-12d3-a456-426614174000",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private UUID caseId;

  @Schema(
      description = "Unique numeric reference for the case",
      example = "100000000000001",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Long caseRef;

  @Schema(description = "Unique Property Reference Number", example = "123456789")
  private String uprn;

  @Schema(description = "Establishment UPRN for non-household establishments")
  private String estabUprn;

  @Schema(description = "Case type classification")
  private String caseType;

  @Schema(description = "Type of address (e.g., HOUSEHOLD, COMMUNAL)")
  private String addressType;

  @Schema(description = "Establishment type code")
  private String estabType;

  @Schema(description = "Address level classification")
  private String addressLevel;

  @Schema(description = "Address Based Priority code")
  private String abpCode;

  @Schema(description = "Name of organization for establishment addresses")
  private String organisationName;

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

  @Schema(description = "Geographic latitude coordinate")
  private String latitude;

  @Schema(description = "Geographic longitude coordinate")
  private String longitude;

  @Schema(description = "Output Area code")
  private String oa;

  @Schema(description = "Lower Layer Super Output Area code")
  private String lsoa;

  @Schema(description = "Middle Layer Super Output Area code")
  private String msoa;

  @Schema(description = "Local Authority District code")
  private String lad;

  @Schema(description = "Administrative region code")
  private String region;

  @Schema(description = "Household Telephone Contact willingness indicator")
  private String htcWillingness;

  @Schema(description = "Household Telephone Contact digital indicator")
  private String htcDigital;

  @Schema(description = "Field Coordinator identifier for the assigned case")
  private String fieldCoordinatorId;

  @Schema(description = "Field Officer identifier for the assigned case")
  private String fieldOfficerId;

  @Schema(description = "Treatment code indicating special handling")
  private String treatmentCode;

  @Schema(description = "Expected capacity for Communal Establishment")
  private Integer ceExpectedCapacity;

  @Schema(description = "Actual number of responses received for Communal Establishment")
  private int ceActualResponses;

  @Schema(description = "Collection Exercise UUID identifier")
  private UUID collectionExerciseId;

  @Schema(description = "Date and time when the case was created", example = "2024-01-15T10:30:00Z")
  private OffsetDateTime createdDateTime;

  @Schema(description = "List of events associated with this case")
  private List<CaseDetailsEventDTO> events;

  @Schema(description = "Flag indicating if receipt has been received from respondent")
  private boolean receiptReceived;

  @Schema(
      description = "Type of refusal received (HARD_REFUSAL, SOFT_REFUSAL, or null)",
      example = "SOFT_REFUSAL")
  private RefusalType refusalReceived;

  @Schema(description = "Flag indicating if the case record is marked as invalid")
  private boolean invalid;

  @Schema(
      description = "Date and time when the case was last updated",
      example = "2024-01-20T14:45:00Z")
  private OffsetDateTime lastUpdated;

  @Schema(description = "Print batch identifier for printed case materials")
  private String printBatch;

  @Schema(description = "Flag indicating if survey has been launched to respondent")
  private boolean surveyLaunched;
}
