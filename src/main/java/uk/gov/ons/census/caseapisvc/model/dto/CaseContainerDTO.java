package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;

@Data
public class CaseContainerDTO {
  private String caseRef;

  @JsonProperty("id")
  private String caseId;

  private String estabType;

  private String uprn;

  private String estabUprn;

  private String collectionExerciseId;

  private String surveyType;

  private String addressType;

  private String caseType;

  private OffsetDateTime createdDateTime;

  private String addressLine1;

  private String addressLine2;

  private String addressLine3;

  private String townName;

  private String postcode;

  private String organisationName;

  private String addressLevel;

  private String abpCode;

  private String region;

  private String latitude;

  private String longitude;

  private String oa;

  private String lsoa;

  private OffsetDateTime lastUpdated;

  private String msoa;

  private String lad;

  private List<CaseEventDTO> caseEvents;

  private boolean handDelivery;

  private Boolean secureEstablishment;

  private boolean invalidAddress;
}
