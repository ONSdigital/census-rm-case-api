package uk.gov.ons.census.casesvc.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.ons.census.casesvc.exception.CaseNotFoundException;
import uk.gov.ons.census.casesvc.model.entity.Case;
import uk.gov.ons.census.casesvc.model.repository.CaseRepository;

public class CaseServiceTest {

  private UUID TEST1_CASE_ID = UUID.randomUUID();
  private Long TEST1_CASE_REFERENCE_ID = 123L;

  private UUID TEST2_CASE_ID = UUID.randomUUID();
  private Long TEST2_CASE_REFERENCE_ID = 456L;

  private UUID TEST3_CASE_ID = UUID.randomUUID();
  private Long TEST3_CASE_REFERENCE_ID = 789L;

  private String TEST_UPRN = "123";

  @Mock private CaseRepository caseRepo;

  @InjectMocks private CaseService caseService;

  @Before
  public void setUp() {
    initMocks(this);
  }

  @Test
  public void shouldReturnCaseWhenCaseIdExists() {

    Case expectedCase = create1TestCase();

    when(caseRepo.findByCaseId(any())).thenReturn(Optional.of(expectedCase));

    Case actualCase = caseService.findByCaseId(TEST1_CASE_ID);
    assertThat(actualCase).isEqualTo(expectedCase);

    ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
    verify(caseRepo).findByCaseId(captor.capture());
    UUID actualCaseId = captor.getValue();
    assertThat(actualCaseId).isEqualTo(expectedCase.getCaseId());
  }

  @Test(expected = CaseNotFoundException.class)
  public void shouldThrowCaseNotFoundExceptionWhenCaseIdDoesNotExist() {

    when(caseRepo.findByCaseId(any())).thenReturn(Optional.empty());

    caseService.findByCaseId(TEST1_CASE_ID);
  }

  @Test
  public void shouldReturnAtLeastOneCaseWhenUPRNExists() {

    List<Case> expectedCases = create3TestCases();

    when(caseRepo.findByuprn(anyString())).thenReturn(Optional.of(create3TestCases()));

    List<Case> actualCases = caseService.findByUPRN(TEST_UPRN);
    assertThat(actualCases, is(expectedCases));

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(caseRepo).findByuprn(captor.capture());
    String actualCaseId = captor.getValue();
    assertThat(actualCaseId).isEqualTo(TEST_UPRN);
  }

  @Test(expected = CaseNotFoundException.class)
  public void shouldThrowCaseNotFoundExceptionWhenUPRNDoesNotExist() {

    when(caseRepo.findByCaseId(any())).thenReturn(Optional.empty());

    caseService.findByUPRN(TEST_UPRN);
  }

  @Test
  public void shouldReturnCaseWhenCaseReferenceExists() {

    Case expectedCase = create1TestCase();

    when(caseRepo.findByCaseRef(anyLong())).thenReturn(Optional.of(expectedCase));

    Case actualCase = caseService.findByReference(TEST1_CASE_REFERENCE_ID);
    assertThat(actualCase).isEqualTo(expectedCase);

    ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
    verify(caseRepo).findByCaseRef(captor.capture());
    Long actualReference = captor.getValue();
    assertThat(actualReference).isEqualTo(TEST1_CASE_REFERENCE_ID);
  }

  @Test(expected = CaseNotFoundException.class)
  public void shouldThrowCaseNotFoundExceptionWhenCaseReferenceDoesNotExist() {

    when(caseRepo.findByCaseId(any())).thenReturn(Optional.empty());

    caseService.findByReference(TEST1_CASE_REFERENCE_ID);
  }

  private Case create1TestCase() {
    return createTestCase(TEST1_CASE_ID, TEST_UPRN, TEST1_CASE_REFERENCE_ID);
  }

  private List<Case> create3TestCases() {
    return Arrays.asList(
        createTestCase(TEST1_CASE_ID, TEST_UPRN, TEST1_CASE_REFERENCE_ID),
        createTestCase(TEST2_CASE_ID, TEST_UPRN, TEST2_CASE_REFERENCE_ID),
        createTestCase(TEST3_CASE_ID, TEST_UPRN, TEST3_CASE_REFERENCE_ID));
  }

  private Case createTestCase(UUID caseId, String uprn, Long caseRef) {
    return Case.builder().caseId(caseId).uprn(uprn).caseRef(caseRef).build();
  }
}
