package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class CaseContainerDTO {

  private String caseRef;

  @JsonProperty("id")
  private String caseId;

  private String arid;

  private String estabArid;

  private String estabType;

  private String uprn;

  @JsonProperty("caseType")
  private String addressType;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  private Date createdDateTime;

  private String addressLine1;

  private String addressLine2;

  private String addressLine3;

  private String townName;

  private String postcode;

  private String organisationName;

  private String addressLevel;

  private String abpCode;

  @JsonProperty("region")
  private String rgn;

  private String latitude;

  private String longitude;

  private String oa;

  private String lsoa;

  private String msoa;

  private String lad;

  private String state;

  private List<EventDTO> caseEvents;
}
