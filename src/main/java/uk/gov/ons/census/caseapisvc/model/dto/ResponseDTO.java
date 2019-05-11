package uk.gov.ons.census.caseapisvc.model.dto;

import lombok.Data;

@Data
public class ResponseDTO {
  private String dateTime;
  private String inboundChannel;
}
