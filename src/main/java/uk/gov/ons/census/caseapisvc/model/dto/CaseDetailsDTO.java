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
        "{\"id\":\"a11e3456-e89b-12d3-a456-426614174000\",\"caseRef\":100000000000001,\"uprn\":\"10008677190\",\"estabUprn\":\"10008677190\",\"caseType\":\"HH\",\"addressType\":\"HH\",\"estabType\":\"HOUSEHOLD\",\"addressLevel\":\"U\",\"abpCode\":\"RD06\",\"organisationName\":\"Acme Corporation\",\"addressLine1\":\"Flat 51 Francombe House\",\"addressLine2\":\"Commercial Road\",\"addressLine3\":\"Suite 3\",\"townName\":\"Windleybury\",\"postcode\":\"XX1 0XX\",\"latitude\":\"51.5074\",\"longitude\":\"-0.1278\",\"oa\":\"E00073438\",\"lsoa\":\"E01014540\",\"msoa\":\"E02003043\",\"lad\":\"E06000023\",\"region\":\"E12000009\",\"htcWillingness\":\"3\",\"htcDigital\":\"4\",\"fieldCoordinatorId\":\"FC12344\",\"fieldOfficerId\":\"FO12345\",\"treatmentCode\":\"HH_PSCE\",\"ceExpectedCapacity\":1505,\"ceActualResponses\":1504,\"collectionExerciseId\":\"b22e3456-e89b-12d3-a456-426614174000\",\"createdDateTime\":\"2024-01-15T10:30:00Z\",\"events\":[],\"receiptReceived\":true,\"refusalReceived\":\"HARD_REFUSAL\",\"invalid\":false,\"lastUpdated\":\"2024-01-20T14:45:00Z\",\"printBatch\":\"15\",\"surveyLaunched\":true}")
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

  @Schema(description = "Unique Property Reference Number", example = "10008677190")
  private String uprn;

  @Schema(
      description = "Establishment UPRN for non-household establishments",
      example = "10008677190")
  private String estabUprn;

  @Schema(
      description =
          "Case type classification (e.g., HH, HI, CE). It will match addressType unless it is an individual (HI) case.",
      example = "HH")
  private String caseType;

  @Schema(description = "Residential address frame type (e.g., HH, CE)", example = "HH")
  private String addressType;

  @Schema(
      description =
          "Establishment type (e.g., HALL OF RESIDENCE, HOUSEHOLD, SHELTERED ACCOMMODATION, RESIDENTIAL CARAVAN, RESIDENTIAL BOAT)",
      example = "HOUSEHOLD")
  private String estabType;

  @Schema(
      description = "Address level classification (e.g., E, U (signifying Establishment and Unit))",
      example = "U")
  private String addressLevel;

  @Schema(description = "AddressBase classification code", example = "RD06")
  private String abpCode;

  @Schema(description = "Name of the organisation at the address", example = "Acme Corporation")
  private String organisationName;

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

  @Schema(description = "Geographic latitude coordinate", example = "51.5074")
  private String latitude;

  @Schema(description = "Geographic longitude coordinate", example = "-0.1278")
  private String longitude;

  @Schema(
      description = "Output Area grid reference (e.g., N00073438, S00073438, E00073438, W00073438)",
      example = "E00073438")
  private String oa;

  @Schema(
      description =
          "Lower Layer Super Output Area grid reference (e.g., N01014540, S01014540, E01014540, W01014540)",
      example = "E01014540")
  private String lsoa;

  @Schema(
      description =
          "Middle Layer Super Output Area grid reference (e.g., N02003043, S02003043, E02003043, W02003043)",
      example = "E02003043")
  private String msoa;

  @Schema(
      description =
          "Local Authority District code (e.g., N06000023, S06000023, E06000023, W06000023)",
      example = "E06000023")
  private String lad;

  @Schema(
      description = "Administrative region code (e.g., N12000009, S12000009, E12000009, W12000009)",
      example = "E12000009")
  private String region;

  @Schema(description = "Hard to Count Index - Willingness (1-5) indicator", example = "3")
  private String htcWillingness;

  @Schema(description = "Hard to Count Index - Digital (1-5) indicator", example = "4")
  private String htcDigital;

  @Schema(description = "Field Coordinator identifier for the assigned case", example = "FC12344")
  private String fieldCoordinatorId;

  @Schema(description = "Field Officer identifier for the assigned case", example = "FO12345")
  private String fieldOfficerId;

  @Schema(
      description =
          "Treatment code (one of the appropriate ones for the region (e.g., HH_PSCE, HH_PSLE, HH_PNCE, HH_PNLE, HH_OSCE, HH_OSLE, HH_ONCE, HH_ONLE, HH_PSCW, HH_PSLW, HH_PNCW, HH_PN, HH_OSCW, HH_OSLW, HH_ONCW, HH_ONLW, HH_OGXS, HH_OSXS, HH_PBXN, HH_OAXN, HH_OBXN)) indicating special handling or processing instructions for the case",
      example = "HH_PSCE")
  private String treatmentCode;

  @Schema(
      description = "Expected resident capacity (bedspaces) of communal establishments (CE)",
      example = "1505")
  private Integer ceExpectedCapacity;

  @Schema(
      description = "Actual number of responses received for Communal Establishment",
      example = "1504")
  private int ceActualResponses;

  // TODO: As per the Sample spec 2027, CE_SECURE (Flag denoting address is a Secure CE) needs to be
  // added to the CaseDetailsDTO. This will be a boolean field.

  @Schema(
      description = "Collection Exercise UUID identifier",
      example = "b22e3456-e89b-12d3-a456-426614174000")
  private UUID collectionExerciseId;

  @Schema(description = "Date and time when the case was created", example = "2024-01-15T10:30:00Z")
  private OffsetDateTime createdDateTime;

  @Schema(description = "List of events associated with this case")
  private List<CaseDetailsEventDTO> events;

  @Schema(
      description = "Flag indicating if receipt has been received from respondent",
      example = "true")
  private boolean receiptReceived;

  @Schema(
      description = "Type of refusal received (HARD_REFUSAL, EXTRAORDINARY_REFUSAL, or null)",
      example = "EXTRAORDINARY_REFUSAL")
  private RefusalType refusalReceived;

  @Schema(
      description = "Flag indicating if the case record is marked as invalid",
      example = "false")
  private boolean invalid;

  @Schema(
      description = "Date and time when the case was last updated",
      example = "2024-01-20T14:45:00Z")
  private OffsetDateTime lastUpdated;

  @Schema(
      description = "Print batch identifier for household initial contact material",
      example = "15")
  private String printBatch;

  @Schema(
      description = "Flag indicating if survey has been launched to respondent",
      example = "true")
  private boolean surveyLaunched;
}
