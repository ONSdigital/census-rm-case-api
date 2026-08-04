package uk.gov.ons.census.caseapisvc.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description = "Enumeration of all possible event types that can occur in the census RM system",
    example = "NEW_CASE",
    enumAsRef = true)
public enum EventTypeDTO {
  @Schema(description = "New case created")
  NEW_CASE,
  @Schema(description = "Receipt received from respondent")
  RECEIPT,
  @Schema(description = "Refusal received from respondent")
  REFUSAL,
  @Schema(description = "Case marked as invalid")
  INVALID_CASE,
  @Schema(description = "EQ (eCollect) questionnaire launched")
  EQ_LAUNCH,
  @Schema(description = "UAC authenticated by respondent")
  UAC_AUTHENTICATION,
  @Schema(description = "Print fulfillment request processed")
  PRINT_FULFILMENT,
  @Schema(description = "Data export file generated")
  EXPORT_FILE,
  @Schema(description = "UAC deactivated")
  DEACTIVATE_UAC,
  @Schema(description = "Sample data updated")
  UPDATE_SAMPLE,
  @Schema(description = "Sample sensitive data updated")
  UPDATE_SAMPLE_SENSITIVE,
  @Schema(description = "SMS fulfillment request processed")
  SMS_FULFILMENT,
  @Schema(description = "SMS fulfillment action rule triggered")
  ACTION_RULE_SMS_REQUEST,
  @Schema(description = "Email fulfillment request processed")
  EMAIL_FULFILMENT,
  @Schema(description = "Email fulfillment action rule triggered")
  ACTION_RULE_EMAIL_REQUEST,
  @Schema(description = "SMS confirmation action rule triggered")
  ACTION_RULE_SMS_CONFIRMATION,
  @Schema(description = "Email confirmation action rule triggered")
  ACTION_RULE_EMAIL_CONFIRMATION,
  @Schema(description = "Data erase requested")
  ERASE_DATA
}
