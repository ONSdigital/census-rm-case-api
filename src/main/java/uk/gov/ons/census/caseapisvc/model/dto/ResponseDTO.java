package uk.gov.ons.census.caseapisvc.model.dto;

import lombok.Data;

@Data
class ResponseDTO {
  private String dateTime;
  private String inboundChannel;
}
