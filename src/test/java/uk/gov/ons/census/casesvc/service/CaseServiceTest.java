package uk.gov.ons.census.casesvc.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyObject;
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

  private static final String METHOD_NAME_FIND_CASE_BY_ID = "findByCaseId";

  private UUID TEST1_CASE_ID = UUID.randomUUID();
  private Long TEST1_CASE_REFERENCE_ID = 123L;

  private UUID TEST2_CASE_ID = UUID.randomUUID();
  private Long TEST2_CASE_REFERENCE_ID = 456L;

  private UUID TEST3_CASE_ID = UUID.randomUUID();
  private Long TEST3_CASE_REFERENCE_ID = 789L;

  private String TEST_UPRN = "123";

  @Mock private CaseRepository caseRepo;

  @InjectMocks private CaseService caseService;
  private UUID actualCaseId;

  @Before
  public void setUp() {
    initMocks(this);
  }

  @Test
  public void shouldReturnCaseWhenCaseIdExists() {

    Case expectedCase = create1TestCase();

    when(caseRepo.findByCaseId(anyObject())).thenReturn(Optional.ofNullable(expectedCase));

    Case actualCase = caseService.findByCaseId(TEST1_CASE_ID);
    assertThat(actualCase).isEqualTo(expectedCase);

    ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
    verify(caseRepo).findByCaseId(captor.capture());

    UUID actualCaseId = captor.getValue();
    assertThat(actualCaseId).isEqualTo(expectedCase.getCaseId());
  }

  @Test(expected = CaseNotFoundException.class)
  public void shouldThrowCaseNotFoundExceptionWhenCaseIdDoesNotExist() {

    Optional<Case> expectedCase = Optional.empty();

    when(caseRepo.findByCaseId(anyObject())).thenReturn(expectedCase);

    caseService.findByCaseId(TEST1_CASE_ID);
  }

  //  @Test
  //  public void shouldReturnCasesWhenUPRNExists() {
  //
  //    List<Case> expectedCases = create3TestCases();
  //
  //    when(caseRepo.findByuprn(anyString())).thenReturn(Optional.ofNullable(expectedCases));
  //
  //    List<Case> actualCases = caseService.findByUPRN(TEST_UPRN);
  //    assertEquals(actualCases, containsInAnyOrder(expectedCases));
  //
  //    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
  //
  //    verify(caseRepo).findByuprn(captor.capture());
  //
  //    String actualCaseId = captor.getValue();
  //    assertThat(actualCaseId).isEqualTo(expectedCase.getCaseId());
  //  }
  //
  //  @Test(expected = CaseNotFoundException.class)
  //  public void shouldThrowCaseNotFoundExceptionWhenCaseIdDoesNotExist() {
  //
  //    Optional<Case> expectedCase = Optional.empty();
  //
  //    when(caseRepo.findByCaseId(anyObject())).thenReturn(expectedCase);
  //
  //    caseService.findByCaseId(TEST1_CASE_ID);
  //  }

  @Test
  public void shouldReturnCaseWhenCaseReferenceExists() {

    Case expectedCase = create1TestCase();

    when(caseRepo.findByCaseRef(anyLong())).thenReturn(Optional.ofNullable(expectedCase));

    Case actualCase = caseService.findByReference(TEST1_CASE_REFERENCE_ID);
    assertThat(actualCase).isEqualTo(expectedCase);

    ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
    verify(caseRepo).findByCaseRef(captor.capture());

    Long actualReference = captor.getValue();
    assertThat(actualReference).isEqualTo(TEST1_CASE_REFERENCE_ID);
  }

  Case expectedCase = createTestCase(TEST1_CASE_ID, TEST_UPRN, TEST1_CASE_REFERENCE_ID);

  @Test(expected = CaseNotFoundException.class)
  public void shouldThrowCaseNotFoundExceptionWhenCaseReferenceDoesNotExist() {

    Optional<Case> expectedCase = Optional.empty();

    when(caseRepo.findByCaseId(anyObject())).thenReturn(expectedCase);

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
