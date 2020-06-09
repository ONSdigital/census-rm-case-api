package uk.gov.ons.census.caseapisvc.model.entity;

public enum EventType {
  CASE_CREATED,
  UAC_UPDATED,
  PRINT_CASE_SELECTED,
  RESPONSE_RECEIVED,
  REFUSAL_RECEIVED,
  FULFILMENT_REQUESTED,
  QUESTIONNAIRE_LINKED,
  SAMPLE_LOADED,
  RM_UAC_CREATED,
  ADDRESS_NOT_VALID,
  ADDRESS_MODIFIED,
  ADDRESS_TYPE_CHANGED,
  NEW_ADDRESS_REPORTED,
  SURVEY_LAUNCHED,
  FIELD_CASE_SELECTED,
  RESPONDENT_AUTHENTICATED,
  UNDELIVERED_MAIL_REPORTED,
  CCS_ADDRESS_LISTED,
  QUESTIONNAIRE_UNLINKED,
  FULFILMENT_CONFIRMED,
  FIELD_CASE_UPDATED
}
