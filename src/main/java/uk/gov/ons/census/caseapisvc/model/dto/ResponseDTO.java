package uk.gov.ons.census.caseapisvc.model.dto;

import java.time.OffsetDateTime;
import lombok.Data;

@Data
class ResponseDTO {
  private OffsetDateTime dateTime;
  private String inboundChannel;
}
