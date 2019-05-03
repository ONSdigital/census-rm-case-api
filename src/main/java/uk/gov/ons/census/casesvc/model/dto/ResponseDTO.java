package uk.gov.ons.census.casesvc.model.dto;

import lombok.Data;

@Data
public class ResponseDTO {
  private String dateTime = "";
  private String inboundChannel = "";
}
