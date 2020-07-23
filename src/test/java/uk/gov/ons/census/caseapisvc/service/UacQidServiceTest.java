package uk.gov.ons.census.caseapisvc.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.CREATED_UAC;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createUacQidCreatedPayload;

import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.ons.census.caseapisvc.client.UacQidServiceClient;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedPayloadDTO;

public class UacQidServiceTest {

  private String NEW_QID = "newly created QID";
  private String ADDRESS_LEVEL_UNIT = "U";
  private String ADDRESS_LEVEL_ESTABLISHMENT = "E";
  private int TEST_QUESTIONNAIRE_TYPE = 1;

  @Mock private UacQidServiceClient uacQidServiceClient;

  @InjectMocks private UacQidService uacQidService;

  @Before
  public void setUp() {
    initMocks(this);
  }

  @Test
  public void createAndLinkUacQid() {
    // Given
    UUID caseId = UUID.randomUUID();
    when(uacQidServiceClient.generateUacQid(eq(TEST_QUESTIONNAIRE_TYPE)))
        .thenReturn(createUacQidCreatedPayload(NEW_QID));

    // When
    UacQidCreatedPayloadDTO uacQidCreatedPayload =
        uacQidService.createAndLinkUacQid(caseId, TEST_QUESTIONNAIRE_TYPE);

    // Then
    assertThat(uacQidCreatedPayload.getCaseId()).isEqualTo(caseId);
    assertThat(uacQidCreatedPayload.getQid()).isEqualTo(NEW_QID);
    assertThat(uacQidCreatedPayload.getUac()).isEqualTo(CREATED_UAC);
  }

  @Test
  public void createAndLinkUacQidReturnsALovelyObjectWhichIAmVeryFondOf() {
    // Given
    UUID caseId = UUID.randomUUID();
    UacQidCreatedPayloadDTO expectedResult = createUacQidCreatedPayload(NEW_QID);
    when(uacQidServiceClient.generateUacQid(eq(TEST_QUESTIONNAIRE_TYPE)))
        .thenReturn(expectedResult);

    // When
    UacQidCreatedPayloadDTO actualResult =
        uacQidService.createAndLinkUacQid(caseId, TEST_QUESTIONNAIRE_TYPE);

    assertThat(actualResult).isEqualTo(expectedResult);
  }

  @Test
  public void calculateQuestionnaireTypeForHouseholdEngland() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("HH", "E1000", ADDRESS_LEVEL_UNIT);

    // Then
    assertThat(questionnaireType).isEqualTo(1);
  }

  @Test
  public void calculateQuestionnaireTypeForHouseholdWales() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("HH", "W1000", ADDRESS_LEVEL_UNIT);

    // Then
    assertThat(questionnaireType).isEqualTo(2);
  }

  @Test
  public void calculateQuestionnaireTypeForHouseholdNI() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("HH", "N1000", ADDRESS_LEVEL_UNIT);

    // Then
    assertThat(questionnaireType).isEqualTo(4);
  }

  @Test
  public void calculateIndividualQuestionnaireTypeForCeUnitEngland() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("CE", "E1000", ADDRESS_LEVEL_UNIT, true);

    // Then
    assertThat(questionnaireType).isEqualTo(21);
  }

  @Test
  public void calculateIndividualQuestionnaireTypeForCeUnitWales() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("CE", "W1000", ADDRESS_LEVEL_UNIT, true);

    // Then
    assertThat(questionnaireType).isEqualTo(22);
  }

  @Test
  public void calculateIndividualQuestionnaireTypeForCeUnitNI() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("CE", "N1000", ADDRESS_LEVEL_UNIT, true);

    // Then
    assertThat(questionnaireType).isEqualTo(24);
  }

  @Test
  public void calculateQuestionnaireTypeForIndividualHHEngland() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("HH", "E1000", ADDRESS_LEVEL_UNIT, true);

    // Then
    assertThat(questionnaireType).isEqualTo(21);
  }

  @Test
  public void calculateQuestionnaireTypeForIndividualHHWales() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("HH", "W1000", ADDRESS_LEVEL_UNIT, true);

    // Then
    assertThat(questionnaireType).isEqualTo(22);
  }

  @Test
  public void calculateQuestionnaireTypeForIndividualHHNI() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("HH", "N1000", ADDRESS_LEVEL_UNIT, true);

    // Then
    assertThat(questionnaireType).isEqualTo(24);
  }

  @Test
  public void calculateQuestionnaireTypeForSpgEnglandUnit() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("SPG", "E1000", ADDRESS_LEVEL_UNIT);

    // Then
    assertThat(questionnaireType).isEqualTo(1);
  }

  @Test
  public void calculateQuestionnaireTypeForSpgWalesUnit() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("SPG", "W1000", ADDRESS_LEVEL_UNIT);

    // Then
    assertThat(questionnaireType).isEqualTo(2);
  }

  @Test
  public void calculateQuestionnaireTypeForSpgNIUnit() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("SPG", "N1000", ADDRESS_LEVEL_UNIT);

    // Then
    assertThat(questionnaireType).isEqualTo(4);
  }

  @Test
  public void calculateQuestionnaireTypeForSpgEnglandEstab() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("SPG", "E1000", ADDRESS_LEVEL_ESTABLISHMENT);

    // Then
    assertThat(questionnaireType).isEqualTo(1);
  }

  @Test
  public void calculateQuestionnaireTypeForSpgWalesEstab() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("SPG", "W1000", ADDRESS_LEVEL_ESTABLISHMENT);

    // Then
    assertThat(questionnaireType).isEqualTo(2);
  }

  @Test
  public void calculateQuestionnaireTypeForSpgNiEstab() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("SPG", "N1000", ADDRESS_LEVEL_ESTABLISHMENT);

    // Then
    assertThat(questionnaireType).isEqualTo(4);
  }

  @Test
  public void calculateQuestionnaireTypeForIndividualSpgEnglandEstab() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("SPG", "E1000", ADDRESS_LEVEL_ESTABLISHMENT, true);

    // Then
    assertThat(questionnaireType).isEqualTo(21);
  }

  @Test
  public void calculateQuestionnaireTypeForIndividualSpgWalesEstab() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("SPG", "W1000", ADDRESS_LEVEL_ESTABLISHMENT, true);

    // Then
    assertThat(questionnaireType).isEqualTo(22);
  }

  @Test
  public void calculateQuestionnaireTypeForIndividualSpgNiEstab() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("SPG", "N1000", ADDRESS_LEVEL_ESTABLISHMENT, true);

    // Then
    assertThat(questionnaireType).isEqualTo(24);
  }

  @Test
  public void calculateQuestionnaireTypeForIndividualSpgEUnit() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("SPG", "E1000", ADDRESS_LEVEL_UNIT, true);

    // Then
    assertThat(questionnaireType).isEqualTo(21);
  }

  @Test
  public void calculateQuestionnaireTypeForIndividualSpgWUnit() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("SPG", "W1000", ADDRESS_LEVEL_UNIT, true);

    // Then
    assertThat(questionnaireType).isEqualTo(22);
  }

  @Test
  public void calculateQuestionnaireTypeForIndividualSpgNiUnit() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("SPG", "N1000", ADDRESS_LEVEL_UNIT, true);

    // Then
    assertThat(questionnaireType).isEqualTo(24);
  }

  @Test
  public void calculateQuestionnaireTypeForIndividualCEEstabEngland() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("CE", "E1000", ADDRESS_LEVEL_ESTABLISHMENT, true);

    // Then
    assertThat(questionnaireType).isEqualTo(21);
  }

  @Test
  public void calculateQuestionnaireTypeForIndividualCEEstabWales() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("CE", "W1000", ADDRESS_LEVEL_ESTABLISHMENT, true);

    // Then
    assertThat(questionnaireType).isEqualTo(22);
  }

  @Test
  public void calculateQuestionnaireTypeForIndividualCEEstabNI() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("CE", "N1000", ADDRESS_LEVEL_ESTABLISHMENT, true);

    // Then
    assertThat(questionnaireType).isEqualTo(24);
  }

  @Test
  public void calculateQuestionnaireTypeForNonIndividualCeEEstab() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("CE", "E1000", ADDRESS_LEVEL_ESTABLISHMENT, false);

    // Then
    assertThat(questionnaireType).isEqualTo(31);
  }

  @Test
  public void calculateQuestionnaireTypeForNonIndividualCeWEstab() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("CE", "W1000", ADDRESS_LEVEL_ESTABLISHMENT, false);

    // Then
    assertThat(questionnaireType).isEqualTo(32);
  }

  @Test
  public void calculateQuestionnaireTypeForNonIndividualCeNiEstab() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("CE", "N1000", ADDRESS_LEVEL_ESTABLISHMENT, false);

    // Then
    assertThat(questionnaireType).isEqualTo(34);
  }

  @Test(expected = IllegalArgumentException.class)
  public void calculateQuestionnaireTypeUnKnownCaseType() {
    // When, then throws
    UacQidService.calculateQuestionnaireType("UN", "E1000", ADDRESS_LEVEL_UNIT);
  }

  @Test(expected = IllegalArgumentException.class)
  public void calculateQuestionnaireTypeUnKnownCountryCode() {
    // When, then throws
    UacQidService.calculateQuestionnaireType("HH", "Z1000", ADDRESS_LEVEL_UNIT);
  }

  @Test(expected = IllegalArgumentException.class)
  public void calculateQuestionnaireTypeCeInvalidAddressLevel() {
    // When, then throws
    UacQidService.calculateQuestionnaireType("CE", "E1000", "NOT_VALID_AL");
  }
}
