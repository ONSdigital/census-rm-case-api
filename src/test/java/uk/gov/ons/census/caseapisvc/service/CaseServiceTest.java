package uk.gov.ons.census.caseapisvc.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static uk.gov.ons.census.caseapisvc.testutils.DataUtils.createMultipleCasesWithEvents;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.ons.census.caseapisvc.model.repository.CaseRepository;
import uk.gov.ons.census.caseapisvc.model.repository.UacQidLinkRepository;
import uk.gov.ons.census.common.model.entity.Case;
import uk.gov.ons.census.common.model.entity.UacQidLink;

@ExtendWith(MockitoExtension.class)
class CaseServiceTest {
  @Mock CaseRepository caseRepository;
  @Mock UacQidLinkRepository uacQidLinkRepository;

  @InjectMocks CaseService caseService;

  private static final int TEST_CASE_REFERENCE_ID_EXISTS = 123;
  private static final String RM_TELEPHONE_CAPTURE_HOUSEHOLD_INDIVIDUAL = "RM_TC_HI";
  private static final UUID TEST_CASE_ID_EXISTS = UUID.randomUUID();
  private static final UUID TEST_CASE_ID_DOES_NOT_EXIST = UUID.randomUUID();

  private static final String TEST_UPRN = "123";
  public static final String TEST_QID = "test_qid";

  @Test
  public void getMultipleCasesWhenUPRNExists() {
    when(caseRepository.findByUprn(anyString()))
        .thenReturn(Optional.of(createMultipleCasesWithEvents().toList()));

    List<Case> actualCases = caseService.findByUPRN(TEST_UPRN, eq(false));
    assertThat(actualCases.size()).isEqualTo(2);

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(caseRepository).findByUprn(captor.capture());
    String actualCaseId = captor.getValue();
    assertThat(actualCaseId).isEqualTo(TEST_UPRN);
  }

  // ----------------------------------------------------------------------
  // findById
  // ----------------------------------------------------------------------
  @Test
  void findById_returnsCase() {
    UUID id = UUID.randomUUID();
    Case caze = new Case();
    when(caseRepository.findById(id)).thenReturn(Optional.of(caze));

    Case result = caseService.findById(id);

    assertSame(caze, result);
  }

  @Test
  void findById_throwsWhenNotFound() {
    UUID id = UUID.randomUUID();
    when(caseRepository.findById(id)).thenReturn(Optional.empty());

    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class, () -> caseService.findById(id));

    assertTrue(ex.getReason().contains(id.toString()));
  }

  // ----------------------------------------------------------------------
  // findByReference
  // ----------------------------------------------------------------------
  @Test
  void findByReference_returnsCase() {
    long ref = 123L;
    Case caze = new Case();
    when(caseRepository.findByCaseRef(ref)).thenReturn(Optional.of(caze));

    Case result = caseService.findByReference(ref);

    assertSame(caze, result);
  }

  @Test
  void findByReference_throwsWhenNotFound() {
    long ref = 123L;
    when(caseRepository.findByCaseRef(ref)).thenReturn(Optional.empty());

    assertThrows(ResponseStatusException.class, () -> caseService.findByReference(ref));
  }

  // ----------------------------------------------------------------------
  // findByUPRN
  // ----------------------------------------------------------------------
  @Test
  void findByUPRN_validAddressOnly_returnsList() {
    String uprn = "UPRN123";
    List<Case> cases = List.of(new Case());
    when(caseRepository.findByUprnAndInvalidFalse(uprn)).thenReturn(Optional.of(cases));

    List<Case> result = caseService.findByUPRN(uprn, true);

    assertEquals(cases, result);
  }

  @Test
  void findByUPRN_validAddressOnly_throwsWhenNotFound() {
    String uprn = "UPRN123";
    when(caseRepository.findByUprnAndInvalidFalse(uprn)).thenReturn(Optional.empty());

    assertThrows(ResponseStatusException.class, () -> caseService.findByUPRN(uprn, true));
  }

  @Test
  void findByUPRN_allAddresses_returnsList() {
    String uprn = "UPRN123";
    List<Case> cases = List.of(new Case());
    when(caseRepository.findByUprn(uprn)).thenReturn(Optional.of(cases));

    List<Case> result = caseService.findByUPRN(uprn, false);

    assertEquals(cases, result);
  }

  @Test
  void findByUPRN_allAddresses_throwsWhenNotFound() {
    String uprn = "UPRN123";
    when(caseRepository.findByUprn(uprn)).thenReturn(Optional.empty());

    assertThrows(ResponseStatusException.class, () -> caseService.findByUPRN(uprn, false));
  }

  @Test
  void getCaseByCaseId() {
    Case caze = new Case();
    caze.setId(UUID.randomUUID());
    Optional<Case> caseOpt = Optional.of(caze);
    when(caseRepository.findById(any())).thenReturn(caseOpt);

    Case returnedCase = caseService.findById(caze.getId());
    Assertions.assertThat(returnedCase).isEqualTo(caze);
    verify(caseRepository).findById(caze.getId());
  }

  // ----------------------------------------------------------------------
  // findByPostcode
  // ----------------------------------------------------------------------
  @Test
  void findByPostcode_returnsList() {
    String postcode = "SM1 1AA";
    List<Case> cases = List.of(new Case());
    when(caseRepository.findByPostcode(postcode)).thenReturn(cases);

    List<Case> result = caseService.findByPostcode(postcode);

    assertEquals(cases, result);
  }

  // ----------------------------------------------------------------------
  // findCaseByQid
  // ----------------------------------------------------------------------
  @Test
  void findCaseByQid_returnsCase() {
    Case caze = new Case();
    UacQidLink uacQidLink = new UacQidLink();
    uacQidLink.setCaze(caze);
    when(uacQidLinkRepository.findByQid(TEST_QID)).thenReturn(Optional.of(uacQidLink));

    Case result = caseService.findCaseByQid(TEST_QID);

    assertSame(caze, result);
  }

  @Test
  void findCaseByQid_throwsWhenQidNotFound() {
    when(uacQidLinkRepository.findByQid(TEST_QID)).thenReturn(Optional.empty());

    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class, () -> caseService.findCaseByQid(TEST_QID));

    assertTrue(ex.getReason().contains(TEST_QID));
  }

  @Test
  void findCaseByQid_throwsWhenCaseNotLinked() {
    UacQidLink uacQidLink = new UacQidLink();
    when(uacQidLinkRepository.findByQid(TEST_QID)).thenReturn(Optional.of(uacQidLink));

    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class, () -> caseService.findCaseByQid(TEST_QID));

    assertTrue(ex.getReason().contains(TEST_QID));
  }
}
