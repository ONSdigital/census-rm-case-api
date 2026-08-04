package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
@Schema(
    description = "Data Transfer Object containing the payload of an event message",
    example =
        "{\"uac\":{\"uacHash\":\"abc123def456\",\"uac\":\"U123456\",\"qid\":\"Q123456\",\"caseId\":\"a11e3456-e89b-12d3-a456-426614174000\"}}")
public class PayloadDTO {
  @Schema(description = "UAC (Unique Access Code) details contained in this event payload")
  private UacDTO uac;
}
