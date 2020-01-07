package uk.gov.ons.census.caseapisvc.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.TEST_CCS_QID;
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
import uk.gov.ons.census.caseapisvc.exception.CaseIdInvalidException;
import uk.gov.ons.census.caseapisvc.exception.CaseIdNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.CaseReferenceNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.QidNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.UPRNNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.UacQidLinkWithNoCaseException;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.model.entity.UacQidLink;
import uk.gov.ons.census.caseapisvc.model.repository.CaseRepository;
import uk.gov.ons.census.caseapisvc.model.repository.UacQidLinkRepository;

public class CaseServiceTest {

  private static final int TEST_CASE_REFERENCE_ID_EXISTS = 123;

  private static final String TEST_CASE_ID_EXISTS = "2e083ab1-41f7-4dea-a3d9-77f48458b5ca";
  private static final String TEST_CASE_ID_DOES_NOT_EXIST = "590179eb-f8ce-4e2d-8cb6-ca4013a2ccf0";
  private static final String TEST_INVALID_CASE_ID = "anything";

  private static final String TEST_UPRN = "123";
  public static final String TEST_QID = "test_qid";

  @Mock private CaseRepository caseRepo;
  @Mock private UacQidLinkRepository uacQidLinkRepository;

  @InjectMocks private CaseService caseService;

  @Before
  public void setUp() {
    initMocks(this);
  }

  @Test
  public void getMultipleCasesWhenUPRNExists() {
    when(caseRepo.findByUprn(anyString())).thenReturn(Optional.of(createMultipleCasesWithEvents()));

    List<Case> actualCases = caseService.findByUPRN(TEST_UPRN);
    assertThat(actualCases.size()).isEqualTo(2);

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(caseRepo).findByUprn(captor.capture());
    String actualCaseId = captor.getValue();
    assertThat(actualCaseId).isEqualTo(TEST_UPRN);
  }

  @Test(expected = UPRNNotFoundException.class)
  public void shouldThrowUPRNNotFoundExceptionWhenUPRNDoesNotExist() {
    when(caseRepo.findByUprn(any())).thenReturn(Optional.empty());

    caseService.findByUPRN(TEST_UPRN);
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
    assertThat(actualCaseId).isEqualTo(UUID.fromString(TEST_CASE_ID_EXISTS));
  }

  @Test(expected = CaseIdNotFoundException.class)
  public void shouldThrowCaseIdNotFoundExceptionWhenCaseIdDoesNotExist() {
    when(caseRepo.findByCaseId(any())).thenReturn(Optional.empty());

    caseService.findByCaseId(TEST_CASE_ID_DOES_NOT_EXIST);
  }

  @Test(expected = CaseIdInvalidException.class)
  public void shouldThrowCaseIdInvalidExceptionWhenCaseIdDoesNotExist() {
    caseService.findByCaseId(TEST_INVALID_CASE_ID);
  }

  @Test
  public void getCaseWhenCaseReferenceExists() {
    Case expectedCase = createSingleCaseWithEvents();

    when(caseRepo.findByCaseRef(anyInt())).thenReturn(Optional.of(expectedCase));

    Case actualCase = caseService.findByReference(TEST_CASE_REFERENCE_ID_EXISTS);
    assertThat(actualCase).isEqualTo(expectedCase);

    ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
    verify(caseRepo).findByCaseRef(captor.capture());
    Integer actualReference = captor.getValue();
    assertThat(actualReference).isEqualTo(TEST_CASE_REFERENCE_ID_EXISTS);
  }

  @Test(expected = CaseReferenceNotFoundException.class)
  public void shouldThrowCaseReferenceNotFoundExceptionWhenCaseReferenceDoesNotExist() {
    when(caseRepo.findByCaseRef(anyInt())).thenReturn(Optional.empty());

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
            UUID.fromString(TEST_CASE_ID_EXISTS), "CCS"))
        .thenReturn(Optional.of(ccsUacQidLink));

    UacQidLink actualCcsUacQidLink =
        caseService.findUacQidLinkByCaseId(ccsCase.getCaseId().toString());
    assertThat(actualCcsUacQidLink.getQid()).isEqualTo(TEST_CCS_QID);
    assertThat(actualCcsUacQidLink.isActive()).isEqualTo(true);
  }

  @Test(expected = QidNotFoundException.class)
  public void testFindCcsQidByCaseIdNoCcsQidFound() {
    when(uacQidLinkRepository.findOneByCcsCaseIsTrueAndCazeCaseIdAndCazeSurvey(
            UUID.fromString(TEST_CASE_ID_DOES_NOT_EXIST), "CCS"))
        .thenReturn(Optional.empty());
    caseService.findUacQidLinkByCaseId(TEST_CASE_ID_EXISTS);
  }
}
