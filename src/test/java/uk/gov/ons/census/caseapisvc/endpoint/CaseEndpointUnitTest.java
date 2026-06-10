package uk.gov.ons.census.caseapisvc.endpoint;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ons.census.caseapisvc.model.dto.*;
import uk.gov.ons.census.caseapisvc.service.CaseService;
import uk.gov.ons.census.common.model.entity.Case;
import uk.gov.ons.census.common.model.entity.Event;
import uk.gov.ons.census.common.model.entity.EventType;
import uk.gov.ons.census.common.model.entity.UacQidLink;

@ExtendWith(MockitoExtension.class)
class CaseEndpointUnitTest {

  @Mock private CaseService caseService;

  @InjectMocks private uk.gov.ons.census.caseapisvc.endpoint.CaseEndpoint caseEndpoint;

  private Case caze;
  private UUID caseId;

  @BeforeEach
  void setup() {
    caseId = UUID.randomUUID();
    caze = mock(Case.class);

    when(caze.getId()).thenReturn(caseId);
    // when(caze.isInvalid()).thenReturn(false);
    // when(caze.getCreatedAt()).thenReturn(OffsetDateTime.now());
    // when(caze.getLastUpdatedAt()).thenReturn(OffsetDateTime.now());
  }

  // -------------------------------------------------------------------------
  // findCaseById
  // -------------------------------------------------------------------------
  @Test
  void testFindCaseById_NoEvents() {
    when(caseService.findById(caseId)).thenReturn(caze);
    when(caze.getCaseRef()).thenReturn(12345L);

    CaseContainerDTO dto = caseEndpoint.findCaseById(caseId, false);

    assertEquals(caseId, dto.getCaseId());
    assertEquals("12345", dto.getCaseRef());
    assertTrue(dto.getCaseEvents().isEmpty());
    verify(caseService).findById(caseId);
  }

  // -------------------------------------------------------------------------
  // findCaseByReference
  // -------------------------------------------------------------------------
  @Test
  void testFindCaseByReference() {

    when(caseService.findByReference(12345L)).thenReturn(caze);
    when(caze.getCaseRef()).thenReturn(12345L);

    CaseContainerDTO dto = caseEndpoint.findCaseByReference(12345L, false);

    assertEquals(caseId, dto.getCaseId());
    assertEquals("12345", dto.getCaseRef());
    verify(caseService).findByReference(12345L);
  }

  // -------------------------------------------------------------------------
  // findCasesByUPRN
  // -------------------------------------------------------------------------
  @Test
  void testFindCasesByUPRN_WithEvents() {
    // Mock event
    Event event = mock(Event.class);
    when(event.getId()).thenReturn(UUID.randomUUID());
    when(event.getDescription()).thenReturn("TEST_EVENT");
    when(event.getDateTime()).thenReturn(OffsetDateTime.now());
    when(event.getType()).thenReturn(EventType.NEW_CASE);

    // Mock UAC/QID link
    UacQidLink link = mock(UacQidLink.class);
    when(link.getEvents()).thenReturn(List.of(event));

    when(caze.getUacQidLinks()).thenReturn(List.of(link));
    when(caze.getEvents()).thenReturn(List.of(event));

    when(caseService.findByUPRN("123456789", true)).thenReturn(List.of(caze));

    List<CaseContainerDTO> result = caseEndpoint.findCasesByUPRN("123456789", true, true);

    assertEquals(1, result.size());
    assertEquals(2, result.get(0).getCaseEvents().size()); // 1 from UAC, 1 from Case
  }

  // -------------------------------------------------------------------------
  // getCasesByPostcode
  // -------------------------------------------------------------------------
  @Test
  void testGetCasesByPostcode() {
    when(caseService.findByPostcode("AB12CD")).thenReturn(List.of(caze));

    List<CaseContainerDTO> result = caseEndpoint.getCasesByPostcode("AB12CD");

    assertEquals(1, result.size());
    assertEquals(caseId, result.get(0).getCaseId());
    verify(caseService).findByPostcode("AB12CD");
  }

  // -------------------------------------------------------------------------
  // findCaseByQid
  // -------------------------------------------------------------------------
  @Test
  void testFindCaseByQid() {
    when(caze.getAddressType()).thenReturn("HH");
    when(caseService.findCaseByQid("Q123")).thenReturn(caze);

    CaseContainerDTO dto = caseEndpoint.findCaseByQid("Q123");

    assertEquals(caseId, dto.getCaseId());
    assertEquals("HH", dto.getAddressType());
    verify(caseService).findCaseByQid("Q123");
  }

  // -------------------------------------------------------------------------
  // getAllCaseDetailsByCaseId
  // -------------------------------------------------------------------------
  @Test
  void testGetAllCaseDetailsByCaseId() {
    when(caze.getCollectionExercise())
        .thenReturn(mock(uk.gov.ons.census.common.model.entity.CollectionExercise.class));
    when(caze.getCollectionExercise().getId()).thenReturn(UUID.randomUUID());
    when(caseService.findById(caseId)).thenReturn(caze);

    CaseDetailsDTO dto = caseEndpoint.getAllCaseDetailsByCaseId(caseId);

    assertEquals(caseId, dto.getCaseId());
    verify(caseService).findById(caseId);
  }

  // -------------------------------------------------------------------------
  // buildCaseContainerDTO: event mapping
  // -------------------------------------------------------------------------
  @Test
  void testBuildCaseContainerDTO_MapsEventsCorrectly() {
    Event event = mock(Event.class);
    when(event.getId()).thenReturn(UUID.randomUUID());
    when(event.getDescription()).thenReturn("DESC");
    when(event.getDateTime()).thenReturn(OffsetDateTime.now());
    when(event.getType()).thenReturn(EventType.NEW_CASE);

    UacQidLink link = mock(UacQidLink.class);
    when(link.getEvents()).thenReturn(List.of(event));

    when(caze.getUacQidLinks()).thenReturn(List.of(link));
    when(caze.getEvents()).thenReturn(List.of(event));

    CaseContainerDTO dto = invokeBuildCaseContainerDTO(caze, true);

    assertEquals(2, dto.getCaseEvents().size());
    assertEquals("DESC", dto.getCaseEvents().get(0).getDescription());
  }

  // Helper to call private method via reflection
  private CaseContainerDTO invokeBuildCaseContainerDTO(Case caze, boolean includeEvents) {
    try {
      var method =
          uk.gov.ons.census.caseapisvc.endpoint.CaseEndpoint.class.getDeclaredMethod(
              "buildCaseContainerDTO", Case.class, boolean.class);
      method.setAccessible(true);
      return (CaseContainerDTO) method.invoke(caseEndpoint, caze, includeEvents);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
