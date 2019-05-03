package uk.gov.ons.census.casesvc.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.ons.census.casesvc.model.entity.Case;
import uk.gov.ons.census.casesvc.model.repository.CaseRepository;

public class CaseServiceTest {

  private static final String METHOD_NAME_FIND_CASE_BY_ID = "findByCaseId";

  private UUID TEST1_CASE_ID;

  @Mock private CaseRepository caseRepo;

  @InjectMocks private CaseService caseService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    this.TEST1_CASE_ID = UUID.randomUUID();
  }

  @Test
  public void shouldReturnCaseWhenCaseIdExists() {
    Case expectedCase = createTestCase();

    when(caseRepo.findByCaseId(anyObject())).thenReturn(expectedCase);

    caseService.findByCaseId(TEST1_CASE_ID);

    ArgumentCaptor<UUID> uuid = ArgumentCaptor.forClass(UUID.class);
    verify(caseRepo).findByCaseId(uuid.capture());

    UUID actualCaseId = uuid.getValue();

    assertThat(actualCaseId).isEqualTo(expectedCase.getCaseId());
  }

  @Test
  public void shouldReturnNullWhenCaseIdDoesNotExist() {
    when(caseService.findByCaseId(anyObject())).thenReturn(null);

    Case actualCase = caseService.findByCaseId(TEST1_CASE_ID);

    ArgumentCaptor<UUID> uuid = ArgumentCaptor.forClass(UUID.class);
    verify(caseRepo).findByCaseId(uuid.capture());

    UUID actualCaseId = uuid.getValue();

    assertThat(actualCaseId).isEqualTo(TEST1_CASE_ID);
    assertThat(actualCase).isNull();
  }

  private Case createTestCase() {
    Case caze = new Case();

    caze.setCaseId(TEST1_CASE_ID);

    return caze;
  }
}
