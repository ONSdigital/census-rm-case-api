// package uk.gov.ons.ssdc;
//
// import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyLong;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
//
// import java.util.UUID;
// import org.assertj.core.api.Assertions;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import uk.gov.ons.census.caseapisvc.model.repository.CaseRepository;
// import uk.gov.ons.census.caseapisvc.service.CaseService;
// import uk.gov.ons.census.common.model.entity.Case;
//
// @ExtendWith(MockitoExtension.class)
// public class CaseServiceTest_SRM {
//  @Mock CaseRepository caseRepository;
//  @InjectMocks
//  CaseService underTest;
//
//  @Test
//  public void testFindById() {
//    Case caze = new Case();
//    caze.setId(UUID.randomUUID());
//
// Mockito.when(caseRepository.findById(ArgumentMatchers.any())).thenReturn(java.util.Optional.of(caze));
//
//    AssertionsForClassTypes.assertThat(underTest.findById(caze.getId())).isEqualTo(caze);
//    Mockito.verify(caseRepository).findById(caze.getId());
//  }
//
//  @Test
//  public void testFindByIdException() {
//    UUID caseId = UUID.randomUUID();
//
//
// Mockito.when(caseRepository.findById(ArgumentMatchers.any())).thenReturn(java.util.Optional.empty());
//    RuntimeException thrown =
//        Assertions.assertThrows(RuntimeException.class, () -> underTest.findById(caseId));
//
//    Assertions.assertThat(thrown.getMessage())
//        .isEqualTo("404 NOT_FOUND \"Case Id '" + caseId + "' not found\"");
//  }
//
//  @Test
//  public void findByCaseRef() {
//    Case caze = new Case();
//    caze.setCaseRef(3434L);
//
// Mockito.when(caseRepository.findByCaseRef(ArgumentMatchers.anyLong())).thenReturn(java.util.Optional.of(caze));
//
//
// AssertionsForClassTypes.assertThat(underTest.findByReference(caze.getCaseRef())).isEqualTo(caze);
//    Mockito.verify(caseRepository).findByCaseRef(caze.getCaseRef());
//  }
//
//  @Test
//  public void getTestCaseRefException() {
//    Long caseRef = 122L;
//
// Mockito.when(caseRepository.findByCaseRef(ArgumentMatchers.anyLong())).thenReturn(java.util.Optional.empty());
//    RuntimeException thrown =
//        Assertions.assertThrows(RuntimeException.class, () -> underTest.findByReference(caseRef));
//
//    Assertions.assertThat(thrown.getMessage())
//        .isEqualTo("404 NOT_FOUND \"Case Reference '" + caseRef + "' not found\"");
//  }
// }
