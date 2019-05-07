package uk.gov.ons.census.casesvc.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.ons.census.casesvc.utility.DataUtils.create1TestCase;
import static uk.gov.ons.census.casesvc.utility.DataUtils.create3TestCases;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.ons.census.casesvc.model.entity.Case;
import uk.gov.ons.census.casesvc.model.repository.CaseRepository;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.CTPException.Fault;

public class CaseServiceTest {

  private UUID TEST1_CASE_ID = UUID.fromString("2e083ab1-41f7-4dea-a3d9-77f48458b5ca");
  private Long TEST1_CASE_REFERENCE_ID = 123L;

  private String TEST_UPRN = "123";

  @Mock private CaseRepository caseRepo;

  @InjectMocks private CaseService caseService;

  @Before
  public void setUp() {
    initMocks(this);
  }

  @Test
  public void shouldReturnAtLeastOneCaseWhenUPRNExists() throws CTPException {
    List<Case> expectedCases = create3TestCases();

    when(caseRepo.findByuprn(anyString())).thenReturn(Optional.of(create3TestCases()));

    List<Case> actualCases = caseService.findByUPRN(TEST_UPRN);
    assertThat(actualCases.size()).isEqualTo(3);

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(caseRepo).findByuprn(captor.capture());
    String actualCaseId = captor.getValue();
    assertThat(actualCaseId).isEqualTo(TEST_UPRN);
  }

  @Test
  public void shouldThrowCaseNotFoundExceptionWhenUPRNDoesNotExist() {
    when(caseRepo.findByCaseRef(any())).thenReturn(Optional.empty());

    try {
      caseService.findByUPRN(TEST_UPRN);
    } catch (HttpClientErrorException hcee) {
      assertThat(hcee.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(hcee.getStatusText()).isEqualTo(String.format("UPRN '%s' not found", TEST_UPRN));
    } catch (CTPException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void shouldReturnCaseWhenCaseIdExists() throws Exception {
    Case expectedCase = create1TestCase();

    when(caseRepo.findByCaseId(any())).thenReturn(Optional.of(expectedCase));

    Case actualCase = caseService.findByCaseId(TEST1_CASE_ID);
    assertThat(actualCase).isEqualTo(expectedCase);

    ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
    verify(caseRepo).findByCaseId(captor.capture());
    UUID actualCaseId = captor.getValue();
    assertThat(actualCaseId).isEqualTo(expectedCase.getCaseId());
  }

  @Test
  public void shouldThrowCaseNotFoundExceptionWhenCaseIdDoesNotExist() throws Exception {
    when(caseRepo.findByCaseId(any())).thenReturn(Optional.empty());

    try {
      caseService.findByCaseId(TEST1_CASE_ID);
    } catch (CTPException ctpe) {
      assertThat(ctpe.getFault()).isEqualTo(Fault.RESOURCE_NOT_FOUND);
      assertThat(ctpe.getMessage())
          .isEqualTo(String.format("Case Id '%s' not found", TEST1_CASE_ID));
    }
  }

  @Test
  public void shouldReturnCaseWhenCaseReferenceExists() throws CTPException {
    Case expectedCase = create1TestCase();

    when(caseRepo.findByCaseRef(anyLong())).thenReturn(Optional.of(expectedCase));

    Case actualCase = caseService.findByReference(TEST1_CASE_REFERENCE_ID);
    assertThat(actualCase).isEqualTo(expectedCase);

    ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
    verify(caseRepo).findByCaseRef(captor.capture());
    Long actualReference = captor.getValue();
    assertThat(actualReference).isEqualTo(TEST1_CASE_REFERENCE_ID);
  }

  @Test
  public void shouldThrowCaseNotFoundExceptionWhenCaseReferenceDoesNotExist() {
    when(caseRepo.findByCaseRef(anyLong())).thenReturn(Optional.empty());

    try {
      caseService.findByReference(TEST1_CASE_REFERENCE_ID);
    } catch (HttpClientErrorException hcee) {
      assertThat(hcee.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(hcee.getStatusText())
          .isEqualTo(String.format("Case Reference '%s' not found", TEST1_CASE_REFERENCE_ID));
    } catch (CTPException e) {
      e.printStackTrace();
    }
  }
}
