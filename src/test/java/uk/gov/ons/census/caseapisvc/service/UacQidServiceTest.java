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
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedDTO;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedPayloadDTO;

public class UacQidServiceTest {

  @Value("${queueconfig.uac-qid-created-exchange}")
  private String uacQidCreatedExchange;

  private String NEW_QID = "newly created QID";
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
    ArgumentCaptor<UacQidCreatedDTO> uacQidCreatedCaptor =
        ArgumentCaptor.forClass(UacQidCreatedDTO.class);
    verify(rabbitTemplate)
        .convertAndSend(eq(uacQidCreatedExchange), eq(""), uacQidCreatedCaptor.capture());
    UacQidCreatedDTO sentUacQidCreatedDTO = uacQidCreatedCaptor.getValue();
    assertThat(sentUacQidCreatedDTO.getEvent().getType()).isEqualTo("RM_UAC_CREATED");
    assertThat(sentUacQidCreatedDTO.getPayload().getUacQidCreated().getCaseId()).isEqualTo(caseId);
    assertThat(sentUacQidCreatedDTO.getPayload().getUacQidCreated().getQid()).isEqualTo(NEW_QID);
    assertThat(sentUacQidCreatedDTO.getPayload().getUacQidCreated().getUac())
        .isEqualTo(CREATED_UAC);
  }

  @Test
  public void calculateQuestionnaireTypeForHouseholdEngland() {
    // Given
    String treatmentCode = "HH_XXXXE";

    // When
    int questionnaireType = UacQidService.calculateQuestionnaireType(treatmentCode);

    // Then
    assertThat(questionnaireType).isEqualTo(1);
  }

  @Test
  public void calculateQuestionnaireTypeForHouseholdWales() {
    // Given
    String treatmentCode = "HH_XXXXW";

    // When
    int questionnaireType = UacQidService.calculateQuestionnaireType(treatmentCode);

    // Then
    assertThat(questionnaireType).isEqualTo(2);
  }

  @Test
  public void calculateQuestionnaireTypeForHouseholdNI() {
    // Given
    String treatmentCode = "HH_XXXXN";

    // When
    int questionnaireType = UacQidService.calculateQuestionnaireType(treatmentCode);

    // Then
    assertThat(questionnaireType).isEqualTo(4);
  }

  @Test(expected = IllegalArgumentException.class)
  public void calculateQuestionnaireTypeUnKnownCaseType() {
    // Given
    String treatmentCode = "UN_XXXXE";

    // When, then throws
    UacQidService.calculateQuestionnaireType(treatmentCode);
  }

  @Test(expected = IllegalArgumentException.class)
  public void calculateQuestionnaireTypeUnKnownCountryCode() {
    // Given
    String treatmentCode = "HH_XXXXO";

    // When, then throws
    UacQidService.calculateQuestionnaireType(treatmentCode);
  }
}
