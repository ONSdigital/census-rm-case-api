package uk.gov.ons.census.caseapisvc.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class CaseContainerDTO {
  private UUID id;
  private String caseRef;
  private String caseType;
  private String createdDateTime;
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String addressLine4;
  private String townName;
  private String region;
  private String postcode;
  private List<ResponseDTO> responses = new ArrayList<>();
  private List<EventDTO> caseEvents;
}
