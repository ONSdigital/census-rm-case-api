package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
    description = "Data Transfer Object for creating a new QID link to a case",
    example =
        "{\"transactionId\":\"a11e3456-e89b-12d3-a456-426614174000\",\"channel\":\"CONTACT_CENTRE\",\"qidLink\":{\"questionnaireId\":\"Q123456\",\"caseId\":\"a11e3456-e89b-12d3-a456-426614174000\"}}")
public class NewQidLink {
  @Schema(
      description = "Transaction ID for tracing this QID link request",
      example = "a11e3456-e89b-12d3-a456-426614174000")
  UUID transactionId;

  @Schema(
      description =
          "Channel through which this link was created (e.g., CONTACT_CENTRE, PHONE, ONLINE)",
      example = "CONTACT_CENTRE")
  String channel;

  @Schema(description = "QID link details containing questionnaire ID and case ID")
  QidLink qidLink;
}
