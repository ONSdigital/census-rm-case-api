package uk.gov.ons.census.caseapisvc.endpoint;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.ons.census.caseapisvc.model.dto.NewQidLink;
import uk.gov.ons.census.caseapisvc.model.dto.QidLink;
import uk.gov.ons.census.caseapisvc.service.CaseService;
import uk.gov.ons.census.caseapisvc.service.UacQidService;
import uk.gov.ons.census.common.model.entity.Case;
import uk.gov.ons.census.common.model.entity.UacQidLink;

class QidEndpointTest {

  private UacQidService uacQidService;
  private CaseService caseService;
  private QidEndpoint endpoint;

  @BeforeEach
  void setup() {
    uacQidService = mock(UacQidService.class);
    caseService = mock(CaseService.class);
    endpoint = new QidEndpoint(uacQidService, caseService);
  }

  // -------------------------------------------------------------------------
  // GET /qids/{qid}
  // -------------------------------------------------------------------------
  @Test
  void getUacQidLinkByQid_withCase() {
    UUID caseId = UUID.randomUUID();

    UacQidLink link = mock(UacQidLink.class);
    Case caze = mock(Case.class);

    when(link.getQid()).thenReturn("Q123");
    when(link.getCaze()).thenReturn(caze);
    when(caze.getId()).thenReturn(caseId);

    when(uacQidService.findUacQidLinkByQid("Q123")).thenReturn(link);

    QidLink dto = endpoint.getUacQidLinkByQid("Q123");

    assertEquals("Q123", dto.getQuestionnaireId());
    assertEquals(caseId, dto.getCaseId());
  }

  @Test
  void getUacQidLinkByQid_withoutCase() {
    UacQidLink link = mock(UacQidLink.class);
    when(link.getQid()).thenReturn("Q123");
    when(link.getCaze()).thenReturn(null);

    when(uacQidService.findUacQidLinkByQid("Q123")).thenReturn(link);

    QidLink dto = endpoint.getUacQidLinkByQid("Q123");

    assertEquals("Q123", dto.getQuestionnaireId());
    assertNull(dto.getCaseId());
  }

  // -------------------------------------------------------------------------
  // PUT /qids/link
  // -------------------------------------------------------------------------
  @Test
  void putQidLinkToCase_dispatchesEvent() {
    UUID caseId = UUID.randomUUID();

    NewQidLink newQidLink = new NewQidLink();
    QidLink qidLink = new QidLink();
    qidLink.setQuestionnaireId("Q999");
    qidLink.setCaseId(caseId);
    newQidLink.setQidLink(qidLink);

    UacQidLink link = mock(UacQidLink.class);
    Case caze = mock(Case.class);

    when(uacQidService.findUacQidLinkByQid("Q999")).thenReturn(link);
    when(caseService.findById(caseId)).thenReturn(caze);

    endpoint.putQidLinkToCase(newQidLink);

    verify(uacQidService).buildAndSendQuestionnaireLinkedEvent(link, caze, newQidLink);
  }
}
