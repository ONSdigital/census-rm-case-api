package uk.gov.ons.census.caseapisvc.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.TEST_CCS_QID;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.TEST_POSTCODE;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createMultipleCasesWithEvents;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createSingleCaseWithEvents;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createSingleCcsCaseWithCcsQid;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.ons.census.caseapisvc.exception.CaseIdNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.CaseReferenceNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.QidNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.UPRNNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.UacQidLinkWithNoCaseException;
import uk.gov.ons.census.caseapisvc.model.dto.ResponseManagementEvent;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedPayloadDTO;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.model.entity.UacQidLink;
import uk.gov.ons.census.caseapisvc.model.repository.CaseRepository;
import uk.gov.ons.census.caseapisvc.model.repository.UacQidLinkRepository;

public class CaseServiceTest {
  private static final int TEST_CASE_REFERENCE_ID_EXISTS = 123;
  private static final String RM_TELEPHONE_CAPTURE_HOUSEHOLD_INDIVIDUAL = "RM_TC_HI";
  private static final UUID TEST_CASE_ID_EXISTS = UUID.randomUUID();
  private static final UUID TEST_CASE_ID_DOES_NOT_EXIST = UUID.randomUUID();

  private static final String TEST_UPRN = "123";
  public static final String TEST_QID = "test_qid";

  @Mock private CaseRepository caseRepo;
  @Mock private UacQidLinkRepository uacQidLinkRepository;
  @Mock private RabbitTemplate rabbitTemplate;

  @InjectMocks private CaseService caseService;

  @Value("${queueconfig.events-exchange}")
  private String eventsExchange;

  @Value("${queueconfig.fulfilment-event-routing-key}")
  private String fulfilmentEventRoutingKey;

  @Before
  public void setUp() {
    initMocks(this);
  }

  @Test
  public void getMultipleCasesWhenUPRNExists() {
    when(caseRepo.findByUprn(anyString())).thenReturn(Optional.of(createMultipleCasesWithEvents()));

    List<Case> actualCases = caseService.findByUPRN(TEST_UPRN, eq(false));
    assertThat(actualCases.size()).isEqualTo(2);

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(caseRepo).findByUprn(captor.capture());
    String actualCaseId = captor.getValue();
    assertThat(actualCaseId).isEqualTo(TEST_UPRN);
  }

  @Test(expected = UPRNNotFoundException.class)
  public void shouldThrowUPRNNotFoundExceptionWhenUPRNDoesNotExist() {
    when(caseRepo.findByUprn(any())).thenReturn(Optional.empty());

    caseService.findByUPRN(TEST_UPRN, eq(false));
  }

  @Test
  public void getCaseWhenCaseIdExists() {
    Case expectedCase = createSingleCaseWithEvents();

    when(caseRepo.findByCaseId(any())).thenReturn(Optional.of(expectedCase));

    Case actualCase = caseService.findByCaseId(TEST_CASE_ID_EXISTS);
    assertThat(actualCase).isEqualTo(expectedCase);

    ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
    verify(caseRepo).findByCaseId(captor.capture());
    UUID actualCaseId = captor.getValue();
    assertThat(actualCaseId).isEqualTo(TEST_CASE_ID_EXISTS);
  }

  @Test(expected = CaseIdNotFoundException.class)
  public void shouldThrowCaseIdNotFoundExceptionWhenCaseIdDoesNotExist() {
    when(caseRepo.findByCaseId(any())).thenReturn(Optional.empty());

    caseService.findByCaseId(TEST_CASE_ID_DOES_NOT_EXIST);
  }

  @Test
  public void getCaseWhenCaseReferenceExists() {
    Case expectedCase = createSingleCaseWithEvents();

    when(caseRepo.findByCaseRef(anyLong())).thenReturn(Optional.of(expectedCase));

    Case actualCase = caseService.findByReference(TEST_CASE_REFERENCE_ID_EXISTS);
    assertThat(actualCase).isEqualTo(expectedCase);

    ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
    verify(caseRepo).findByCaseRef(captor.capture());
    Long actualReference = captor.getValue();
    assertThat(actualReference).isEqualTo(TEST_CASE_REFERENCE_ID_EXISTS);
  }

  @Test(expected = CaseReferenceNotFoundException.class)
  public void shouldThrowCaseReferenceNotFoundExceptionWhenCaseReferenceDoesNotExist() {
    when(caseRepo.findByCaseRef(anyLong())).thenReturn(Optional.empty());

    caseService.findByReference(TEST_CASE_REFERENCE_ID_EXISTS);
  }

  @Test
  public void testGetCaseViaQid() {
    Case expectedCase = createSingleCaseWithEvents();
    UacQidLink uacQidLink = new UacQidLink();
    uacQidLink.setCaze(expectedCase);
    Optional<UacQidLink> uacQidLinkOptional = Optional.of(uacQidLink);
    when(uacQidLinkRepository.findByQid(TEST_QID)).thenReturn(uacQidLinkOptional);

    Case actualCase = caseService.findCaseByQid(TEST_QID);

    assertThat(actualCase).isEqualToComparingFieldByField(expectedCase);
  }

  @Test(expected = QidNotFoundException.class)
  public void testQidNotFound() {
    when(uacQidLinkRepository.findByQid(TEST_QID)).thenReturn(Optional.empty());
    caseService.findCaseByQid(TEST_QID);
  }

  @Test(expected = UacQidLinkWithNoCaseException.class)
  public void testCaseMissingForUacQidLink() {
    UacQidLink uacQidLink = new UacQidLink();
    Optional<UacQidLink> uacQidLinkOptional = Optional.of(uacQidLink);
    when(uacQidLinkRepository.findByQid(TEST_QID)).thenReturn(uacQidLinkOptional);

    caseService.findCaseByQid(TEST_QID);
  }

  @Test
  public void testFindCcsQidByCaseId() {
    Case ccsCase = createSingleCcsCaseWithCcsQid();
    UacQidLink ccsUacQidLink = ccsCase.getUacQidLinks().get(0);
    when(uacQidLinkRepository.findOneByCcsCaseIsTrueAndCazeCaseIdAndCazeSurvey(
            ccsCase.getCaseId(), "CCS"))
        .thenReturn(Optional.of(ccsUacQidLink));

    UacQidLink actualCcsUacQidLink = caseService.findCCSUacQidLinkByCaseId(ccsCase.getCaseId());
    assertThat(actualCcsUacQidLink.getQid()).isEqualTo(TEST_CCS_QID);
    assertThat(actualCcsUacQidLink.isActive()).isEqualTo(true);
  }

  @Test(expected = QidNotFoundException.class)
  public void testFindCcsQidByCaseIdNoCcsQidFound() {
    when(uacQidLinkRepository.findOneByCcsCaseIsTrueAndCazeCaseIdAndCazeSurvey(
            TEST_CASE_ID_DOES_NOT_EXIST, "CCS"))
        .thenReturn(Optional.empty());
    caseService.findCCSUacQidLinkByCaseId(TEST_CASE_ID_EXISTS);
  }

  @Test
  public void testBuildAndSendHiTelephoneCaptureFulfilmentRequest() {
    // Given
    UUID parentCaseId = UUID.randomUUID();
    UUID individualCaseId = UUID.randomUUID();
    UacQidCreatedPayloadDTO uacQidCreated = new UacQidCreatedPayloadDTO();

    // When
    caseService.buildAndSendTelephoneCaptureFulfilmentRequest(
        parentCaseId, RM_TELEPHONE_CAPTURE_HOUSEHOLD_INDIVIDUAL, individualCaseId, uacQidCreated);

    // Then
    ArgumentCaptor<ResponseManagementEvent> eventArgumentCaptor =
        ArgumentCaptor.forClass(ResponseManagementEvent.class);
    verify(rabbitTemplate)
        .convertAndSend(
            eq(eventsExchange), eq(fulfilmentEventRoutingKey), eventArgumentCaptor.capture());

    ResponseManagementEvent responseManagementEvent = eventArgumentCaptor.getValue();
    assertThat(responseManagementEvent.getEvent().getType()).isEqualTo("FULFILMENT_REQUESTED");
    assertThat(responseManagementEvent.getPayload().getFulfilmentRequest().getCaseId())
        .isEqualTo(parentCaseId);
    assertThat(responseManagementEvent.getPayload().getFulfilmentRequest().getIndividualCaseId())
        .isEqualTo(individualCaseId);
    assertThat(responseManagementEvent.getPayload().getFulfilmentRequest().getFulfilmentCode())
        .isEqualTo("RM_TC_HI");
    assertThat(responseManagementEvent.getPayload().getFulfilmentRequest().getUacQidCreated())
        .isEqualTo(uacQidCreated);
  }

  @Test
  public void testFindCasesByPostcode() {
    // Given
    Case expectedCase = createSingleCaseWithEvents();
    expectedCase.setPostcode(TEST_POSTCODE);

    when(caseRepo.findByPostcode(eq(TEST_POSTCODE))).thenReturn(List.of(expectedCase));

    // When
    List<Case> actualCases = caseService.findByPostcode(TEST_POSTCODE);

    // Then
    assertThat(actualCases).hasSize(1);
    assertThat(actualCases.get(0)).isEqualTo(expectedCase);

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(caseRepo).findByPostcode(captor.capture());
    String actualPostcode = captor.getValue();
    assertThat(actualPostcode).isEqualTo(TEST_POSTCODE);
  }
}
