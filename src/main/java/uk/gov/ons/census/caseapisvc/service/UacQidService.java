package uk.gov.ons.census.caseapisvc.service;

import java.util.UUID;
import org.springframework.stereotype.Service;
import uk.gov.ons.census.caseapisvc.client.UacQidServiceClient;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedPayloadDTO;

@Service
public class UacQidService {
  private static final String ADDRESS_LEVEL_ESTAB = "E";

  private static final String COUNTRY_CODE_ENGLAND = "E";
  private static final String COUNTRY_CODE_WALES = "W";
  private static final String COUNTRY_CODE_NORTHERN_IRELAND = "N";

  private static final String CASE_TYPE_HOUSEHOLD = "HH";
  private static final String CASE_TYPE_SPG = "SPG";
  private static final String CASE_TYPE_CE = "CE";

  private UacQidServiceClient uacQidServiceClient;

  public UacQidService(UacQidServiceClient uacQidServiceClient) {
    this.uacQidServiceClient = uacQidServiceClient;
  }

  public UacQidCreatedPayloadDTO createAndLinkUacQid(UUID caseId, int questionnaireType) {
    UacQidCreatedPayloadDTO uacQidCreatedPayload =
        uacQidServiceClient.generateUacQid(questionnaireType);
    uacQidCreatedPayload.setCaseId(caseId);
    return uacQidCreatedPayload;
  }

  public static int calculateQuestionnaireType(
      String caseType, String region, String addressLevel) {
    return calculateQuestionnaireType(caseType, region, addressLevel, false);
  }

  public static int calculateQuestionnaireType(
      String caseType, String region, String addressLevel, boolean individual) {
    String country = region.substring(0, 1);
    if (!country.equals(COUNTRY_CODE_ENGLAND)
        && !country.equals(COUNTRY_CODE_WALES)
        && !country.equals(COUNTRY_CODE_NORTHERN_IRELAND)) {
      throw new IllegalArgumentException(
          String.format("Unknown Country for treatment code %s", caseType));
    }

    if (individual) {
      switch (country) {
        case COUNTRY_CODE_ENGLAND:
          return 21;
        case COUNTRY_CODE_WALES:
          return 22;
        case COUNTRY_CODE_NORTHERN_IRELAND:
          return 24;
      }
    } else if (isHouseholdCaseType(caseType) || isSpgCaseType(caseType)) {
      switch (country) {
        case COUNTRY_CODE_ENGLAND:
          return 1;
        case COUNTRY_CODE_WALES:
          return 2;
        case COUNTRY_CODE_NORTHERN_IRELAND:
          return 4;
      }
    } else if (isCE1RequestForEstabCeCase(caseType, addressLevel, individual)) {
      switch (country) {
        case COUNTRY_CODE_ENGLAND:
          return 31;
        case COUNTRY_CODE_WALES:
          return 32;
        case COUNTRY_CODE_NORTHERN_IRELAND:
          return 34;
      }
    } else {
      throw new IllegalArgumentException(
          String.format(
              "Unexpected combination of Case Type, Address level and individual request. treatment code: '%s', address level: '%s', individual request: '%s'",
              caseType, addressLevel, individual));
    }

    throw new RuntimeException(
        String.format(
            "Unprocessable combination of Case Type, Address level and individual request. treatment code: '%s', address level: '%s', individual request: '%s'",
            caseType, addressLevel, individual));
  }

  private static boolean isCE1RequestForEstabCeCase(
      String treatmentCode, String addressLevel, boolean individual) {
    return isCeCaseType(treatmentCode) && addressLevel.equals(ADDRESS_LEVEL_ESTAB) && !individual;
  }

  private static boolean isSpgCaseType(String caseType) {
    return caseType.equals(CASE_TYPE_SPG);
  }

  private static boolean isHouseholdCaseType(String caseType) {
    return caseType.equals(CASE_TYPE_HOUSEHOLD);
  }

  private static boolean isCeCaseType(String caseType) {
    return caseType.equals(CASE_TYPE_CE);
  }
}
