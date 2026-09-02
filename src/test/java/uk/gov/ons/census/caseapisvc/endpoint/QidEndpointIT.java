package uk.gov.ons.census.caseapisvc.endpoint;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;
import uk.gov.ons.census.caseapisvc.model.dto.NewQidLink;
import uk.gov.ons.census.caseapisvc.model.dto.QidLink;
import uk.gov.ons.census.caseapisvc.service.CaseService;
import uk.gov.ons.census.caseapisvc.service.UacQidService;
import uk.gov.ons.census.caseapisvc.utility.ObjectMapperFactory;
import uk.gov.ons.census.common.model.entity.Case;
import uk.gov.ons.census.common.model.entity.UacQidLink;

class QidEndpointIT {

  private MockMvc mockMvc;

  private UacQidService uacQidService;

  private CaseService caseService;

  private final JsonMapper objectMapper = (JsonMapper) ObjectMapperFactory.objectMapper();

  @BeforeEach
  void setUp() {
    uacQidService = mock(UacQidService.class);
    caseService = mock(CaseService.class);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new QidEndpoint(uacQidService, caseService))
            .setMessageConverters(new JacksonJsonHttpMessageConverter(objectMapper))
            .build();
  }

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

    var result =
        mockMvc
            .perform(
                put("/qids/link")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newQidLink)))
            .andExpect(status().isNotImplemented());

    // Extract the exception thrown by the controller
    Exception resolved = result.andReturn().getResolvedException();

    ResponseStatusException ex = (ResponseStatusException) resolved;
    Assertions.assertNotNull(ex);
    assertThat(ex.getStatusCode().value()).isEqualTo(501);
    assertThat(ex.getReason())
        .isEqualTo("Questionnaire Id Link is not available, request cannot be fulfilled");
  }
}
