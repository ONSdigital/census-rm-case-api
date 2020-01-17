package uk.gov.ons.census.caseapisvc.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.CREATED_UAC;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createUacQidCreatedPayload;

import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.ons.census.caseapisvc.client.UacQidServiceClient;
import uk.gov.ons.census.caseapisvc.model.dto.ResponseManagementEvent;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedPayloadDTO;

public class UacQidServiceTest {

  @Value("${queueconfig.uac-qid-created-exchange}")
  private String uacQidCreatedExchange;

  private String NEW_QID = "newly created QID";
  private String ADDRESS_LEVEL_UNIT = "U";
  private int TEST_QUESTIONNAIRE_TYPE = 1;

  @Mock private UacQidServiceClient uacQidServiceClient;
  @Mock private RabbitTemplate rabbitTemplate;

  @InjectMocks private UacQidService uacQidService;

  @Before
  public void setUp() {
    initMocks(this);
  }

  @Test
  public void createAndLinkUacQid() {
    // Given
    String caseId = UUID.randomUUID().toString();
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
  public void createAndLinkUacQidSendsRmUacCreatedEvent() {
    // Given
    String caseId = UUID.randomUUID().toString();
    when(uacQidServiceClient.generateUacQid(eq(TEST_QUESTIONNAIRE_TYPE)))
        .thenReturn(createUacQidCreatedPayload(NEW_QID));

    // When
    uacQidService.createAndLinkUacQid(caseId, TEST_QUESTIONNAIRE_TYPE);

    // Then
    ArgumentCaptor<ResponseManagementEvent> uacQidCreatedCaptor =
        ArgumentCaptor.forClass(ResponseManagementEvent.class);
    verify(rabbitTemplate)
        .convertAndSend(eq(uacQidCreatedExchange), eq(""), uacQidCreatedCaptor.capture());
    ResponseManagementEvent sentResponseManagementEvent = uacQidCreatedCaptor.getValue();
    assertThat(sentResponseManagementEvent.getEvent().getType()).isEqualTo("RM_UAC_CREATED");
    assertThat(sentResponseManagementEvent.getPayload().getUacQidCreated().getCaseId())
        .isEqualTo(caseId);
    assertThat(sentResponseManagementEvent.getPayload().getUacQidCreated().getQid())
        .isEqualTo(NEW_QID);
    assertThat(sentResponseManagementEvent.getPayload().getUacQidCreated().getUac())
        .isEqualTo(CREATED_UAC);
  }

  @Test
  public void calculateQuestionnaireTypeForHouseholdEngland() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("HH_XXXXE", ADDRESS_LEVEL_UNIT);

    // Then
    assertThat(questionnaireType).isEqualTo(1);
  }

  @Test
  public void calculateQuestionnaireTypeForHouseholdWales() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("HH_XXXXW", ADDRESS_LEVEL_UNIT);

    // Then
    assertThat(questionnaireType).isEqualTo(2);
  }

  @Test
  public void calculateQuestionnaireTypeForHouseholdNI() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("HH_XXXXN", ADDRESS_LEVEL_UNIT);

    // Then
    assertThat(questionnaireType).isEqualTo(4);
  }

  @Test
  public void calculateQuestionnaireTypeForCeEngland() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("CE_XXXXE", ADDRESS_LEVEL_UNIT);

    // Then
    assertThat(questionnaireType).isEqualTo(21);
  }

  @Test
  public void calculateQuestionnaireTypeForCeWales() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("CE_XXXXW", ADDRESS_LEVEL_UNIT);

    // Then
    assertThat(questionnaireType).isEqualTo(22);
  }

  @Test
  public void calculateQuestionnaireTypeForCeNI() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("CE_XXXXN", ADDRESS_LEVEL_UNIT);

    // Then
    assertThat(questionnaireType).isEqualTo(24);
  }

  @Test
  public void calculateQuestionnaireTypeForIndividualHHEngland() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("HH_XXXXE", ADDRESS_LEVEL_UNIT, true);

    // Then
    assertThat(questionnaireType).isEqualTo(21);
  }

  @Test
  public void calculateQuestionnaireTypeForIndividualHHWales() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("HH_XXXXW", ADDRESS_LEVEL_UNIT, true);

    // Then
    assertThat(questionnaireType).isEqualTo(22);
  }

  @Test
  public void calculateQuestionnaireTypeForIndividualHHNI() {
    // When
    int questionnaireType =
        UacQidService.calculateQuestionnaireType("HH_XXXXN", ADDRESS_LEVEL_UNIT, true);

    // Then
    assertThat(questionnaireType).isEqualTo(24);
  }

  @Test(expected = IllegalArgumentException.class)
  public void calculateQuestionnaireTypeUnKnownCaseType() {
    // When, then throws
    UacQidService.calculateQuestionnaireType("UN_XXXXE", ADDRESS_LEVEL_UNIT);
  }

  @Test(expected = IllegalArgumentException.class)
  public void calculateQuestionnaireTypeUnKnownCountryCode() {
    // When, then throws
    UacQidService.calculateQuestionnaireType("HH_XXXXO", ADDRESS_LEVEL_UNIT);
  }

  @Test(expected = IllegalArgumentException.class)
  public void calculateQuestionnaireTypeCeInvalidAddressLevel() {
    // When, then throws
    UacQidService.calculateQuestionnaireType("CE_XXXXE", "NOT_VALID_AL");
  }
}
