package uk.gov.ons.census.caseapisvc.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Data;

@Data
@Schema(
    description = "Data Transfer Object containing payload for UAC/QID creation event",
    example =
        "{\"uac\":\"U123456\",\"qid\":\"Q123456\",\"caseId\":\"a11e3456-e89b-12d3-a456-426614174000\"}")
public class UacQidCreatedPayloadDTO {
  @Schema(
      description = "Unique Access Code (UAC) string that was created",
      example = "U123456",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String uac;

  @Schema(
      description = "Questionnaire ID (QID) that was created and linked to the UAC",
      example = "Q123456",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String qid;

  @Schema(
      description = "UUID of the case associated with this UAC/QID creation",
      example = "a11e3456-e89b-12d3-a456-426614174000",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private UUID caseId;
}
