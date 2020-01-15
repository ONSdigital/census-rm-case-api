package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class FulfilmentRequestDTO {

  @JsonInclude(Include.NON_NULL)
  private String caseId;

  private String fulfilmentCode;

  @JsonInclude(Include.NON_NULL)
  private String individualCaseId;
}
