package uk.gov.ons.census.caseapisvc.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CaseDetailsDTO {

    @JsonProperty("questionnaire_id")
    private String questionnaireId;
}
