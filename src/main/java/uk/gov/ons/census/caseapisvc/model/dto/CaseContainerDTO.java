package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class CaseContainerDTO {
  @JsonProperty("id")
  private UUID caseId;

  private String caseRef;

  @JsonProperty("caseType")
  private String addressType;

  private String createdDateTime;

  private String addressLine1;

  private String addressLine2;

  private String addressLine3;

  private String addressLine4;

  private String townName;

  @JsonProperty("region")
  private String rgn;

  private String postcode;

  private List<ResponseDTO> responses = new ArrayList<>();

  private List<EventDTO> caseEvents;
}
