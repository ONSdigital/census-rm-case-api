package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
@Schema(
    description =
        "Data Transfer Object representing a UAC (Unique Access Code) and its associated questionnaire details",
    example =
        "{\"uacHash\":\"abc123def456\",\"uac\":\"U123456\",\"questionnaireId\":\"Q123456\",\"caseId\":\"a11e3456-e89b-12d3-a456-426614174000\",\"caseType\":\"HH\",\"active\":true}")
public class UacDTO {
  // Uac block for QUESTIONNAIRE_LINKED events

  @Schema(description = "Hashed value of the UAC for security purposes", example = "abc123def456")
  private String uacHash;

  @Schema(description = "Unique Access Code (UAC) string issued to respondent", example = "U123456")
  private String uac;

  @Schema(
      description = "Flag indicating if the UAC is currently active and can be used",
      example = "true")
  private Boolean active;

  @Schema(description = "Questionnaire ID (QID) associated with this UAC", example = "Q123456")
  private String questionnaireId;

  @Schema(
      description =
          "Type of case associated with this UAC (e.g., HH, HI, CE). It will match addressType unless it is an individual (HI) case.",
      example = "HH")
  private String caseType;

  @Schema(description = "Administrative region code", example = "E12000009")
  private String region;

  @Schema(
      description = "Case UUID associated with this UAC",
      example = "a11e3456-e89b-12d3-a456-426614174000")
  private UUID caseId;

  @Schema(
      description = "Collection Exercise UUID associated with this UAC",
      example = "b22e3456-e89b-12d3-a456-426614174000")
  private UUID collectionExerciseId;

  @Schema(description = "Form type code for the questionnaire (e.g., H, I, C)", example = "H")
  private String formType;

  @Schema(
      description = "Individual case UUID if this is part of an individual questionnaire",
      example = "c33e3456-e89b-12d3-a456-426614")
  private UUID individualCaseId;
}
