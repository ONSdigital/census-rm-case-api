package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.UUID;
import lombok.Data;

@Data
public class FulfilmentRequestDTO {

  @JsonInclude(Include.NON_NULL)
  private UUID caseId;

  private String fulfilmentCode;

  @JsonInclude(Include.NON_NULL)
  private UUID individualCaseId;

  private UacQidCreatedPayloadDTO uacQidCreated;
}
