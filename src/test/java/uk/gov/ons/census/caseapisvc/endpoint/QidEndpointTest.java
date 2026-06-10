package uk.gov.ons.census.caseapisvc.endpoint;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.ons.census.caseapisvc.model.dto.NewQidLink;
import uk.gov.ons.census.caseapisvc.model.dto.QidLink;
import uk.gov.ons.census.caseapisvc.service.CaseService;
import uk.gov.ons.census.caseapisvc.service.UacQidService;
import uk.gov.ons.census.common.model.entity.Case;
import uk.gov.ons.census.common.model.entity.UacQidLink;

@ExtendWith(SpringExtension.class)
@WebMvcTest(QidEndpoint.class)
@ActiveProfiles("test")
class QidEndpointTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UacQidService uacQidService;

  @MockBean private CaseService caseService;

  @Autowired private ObjectMapper objectMapper;

  // -------------------------------------------------------------------------
  // GET /qids/{qid}
  // -------------------------------------------------------------------------
  @Test
  void testGetUacQidLinkByQid() throws Exception {
    UUID caseId = UUID.randomUUID();

    UacQidLink link = mock(UacQidLink.class);
    Case caze = mock(Case.class);

    when(link.getQid()).thenReturn("123456789012");
    when(link.getCaze()).thenReturn(caze);
    when(caze.getId()).thenReturn(caseId);

    when(uacQidService.findUacQidLinkByQid("123456789012")).thenReturn(link);

    mockMvc
        .perform(get("/qids/123456789012"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.questionnaireId").value("123456789012"))
        .andExpect(jsonPath("$.caseId").value(caseId.toString()));
  }

  // -------------------------------------------------------------------------
  // PUT /qids/link
  // -------------------------------------------------------------------------
  @Test
  void testPutQidLinkToCase() throws Exception {
    UUID caseId = UUID.randomUUID();

    // Mock incoming JSON
    NewQidLink newQidLink = new NewQidLink();
    QidLink qidLink = new QidLink();
    qidLink.setQuestionnaireId("111222333444");
    qidLink.setCaseId(caseId);
    newQidLink.setQidLink(qidLink);

    // Mock service layer
    UacQidLink link = mock(UacQidLink.class);
    Case caze = mock(Case.class);

    when(uacQidService.findUacQidLinkByQid("111222333444")).thenReturn(link);

    when(caseService.findById(caseId)).thenReturn(caze);

    mockMvc
        .perform(
            put("/qids/link")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newQidLink)))
        .andExpect(status().isOk());

    verify(uacQidService).buildAndSendQuestionnaireLinkedEvent(link, caze, newQidLink);
  }
}
