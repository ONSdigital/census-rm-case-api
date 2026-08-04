package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
    description =
        "Data Transfer Object representing the link between a Questionnaire ID (QID) and a Case",
    example =
        "{\"questionnaireId\":\"Q123456\",\"caseId\":\"a11e3456-e89b-12d3-a456-426614174000\"}")
public class QidLink {
  @Schema(
      description = "Unique Questionnaire Identifier (QID) assigned to the respondent",
      example = "Q123456",
      requiredMode = Schema.RequiredMode.REQUIRED)
  String questionnaireId;

  @Schema(
      description = "UUID of the census case associated with this QID",
      example = "a11e3456-e89b-12d3-a456-426614174000")
  UUID caseId;
}
