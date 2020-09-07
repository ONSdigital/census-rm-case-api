package uk.gov.ons.census.caseapisvc.endpoint;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createSingleCaseWithEvents;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createUacQidLink;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createUrl;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.mapper;

import java.util.UUID;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ons.census.caseapisvc.exception.CaseIdNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.QidNotFoundException;
import uk.gov.ons.census.caseapisvc.model.dto.QidLink;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.model.entity.UacQidLink;
import uk.gov.ons.census.caseapisvc.service.CaseService;
import uk.gov.ons.census.caseapisvc.service.UacQidService;

public class QidEndpointTest {

  private static final String INVALID_QID = "invalid_qid";

  private MockMvc mockMvc;

  @Mock private CaseService caseService;
  @Mock private UacQidService uacQidService;

  @Spy
  private MapperFacade mapperFacade = new DefaultMapperFactory.Builder().build().getMapperFacade();

  @InjectMocks private QidEndpoint qidEndpoint;

  @Before
  public void setUp() {
    initMocks(this);

    mockMvc = MockMvcBuilders.standaloneSetup(qidEndpoint).build();
  }

  @Test
  public void testGetUacQidLinkByQidNoLinkedCase() throws Exception {
    // Given
    UacQidLink uacQidLink = createUacQidLink();
    when(uacQidService.findUacQidLinkByQid(uacQidLink.getQid())).thenReturn(uacQidLink);

    // When, then
    mockMvc
        .perform(
            get(createUrl("/qids/%s", uacQidLink.getQid()))
                .param("caseEvents", "true")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(QidEndpoint.class))
        .andExpect(jsonPath("$.questionnaireId", is(uacQidLink.getQid())));
  }

  @Test
  public void testGetUacQidLinkByWithLinkedCase() throws Exception {
    // Given
    UacQidLink uacQidLink = createUacQidLink();
    when(uacQidService.findUacQidLinkByQid(uacQidLink.getQid())).thenReturn(uacQidLink);

    Case linkedCase = createSingleCaseWithEvents();
    when(caseService.findByCaseId(linkedCase.getCaseId())).thenReturn(linkedCase);

    uacQidLink.setCaze(linkedCase);

    // When, then
    mockMvc
        .perform(get(createUrl("/qids/%s", uacQidLink.getQid())))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(QidEndpoint.class))
        .andExpect(jsonPath("$.questionnaireId", is(uacQidLink.getQid())))
        .andExpect(jsonPath("$.caseId", is(uacQidLink.getCaze().getCaseId().toString())));
  }

  @Test
  public void testGetUacQidLinkByQidNotFound() throws Exception {
    // Given
    when(uacQidService.findUacQidLinkByQid(INVALID_QID))
        .thenThrow(new QidNotFoundException(INVALID_QID));

    // When, then
    mockMvc
        .perform(get(createUrl("/qids/%s", INVALID_QID)))
        .andExpect(status().isNotFound())
        .andExpect(handler().handlerType(QidEndpoint.class));
  }

  @Test
  public void testLinkQidToCase() throws Exception {
    // Given
    UacQidLink uacQidLink = createUacQidLink();
    when(uacQidService.findUacQidLinkByQid(uacQidLink.getQid())).thenReturn(uacQidLink);

    Case caseToLink = createSingleCaseWithEvents();
    when(caseService.findByCaseId(caseToLink.getCaseId())).thenReturn(caseToLink);

    QidLink qidLink = new QidLink();
    qidLink.setCaseId(caseToLink.getCaseId());
    qidLink.setQuestionnaireId(uacQidLink.getQid());

    // When
    mockMvc
        .perform(
            put("/qids/link")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(qidLink)))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(QidEndpoint.class));

    // Then
    verify(uacQidService).buildAndSendQuestionnaireLinkedEvent(eq(uacQidLink), eq(caseToLink));
  }

  @Test
  public void testLinkQidToCaseQidNotFound() throws Exception {
    // Given
    when(uacQidService.findUacQidLinkByQid(INVALID_QID))
        .thenThrow(new QidNotFoundException(INVALID_QID));
    Case caseToLink = createSingleCaseWithEvents();
    when(caseService.findByCaseId(caseToLink.getCaseId())).thenReturn(caseToLink);

    QidLink qidLink = new QidLink();
    qidLink.setCaseId(caseToLink.getCaseId());
    qidLink.setQuestionnaireId(INVALID_QID);

    // When, then
    mockMvc
        .perform(
            put("/qids/link")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(qidLink)))
        .andExpect(status().isNotFound())
        .andExpect(handler().handlerType(QidEndpoint.class));
  }

  @Test
  public void testLinkQidToCaseCaseNotFound() throws Exception {
    // Given
    UacQidLink uacQidLink = createUacQidLink();
    when(uacQidService.findUacQidLinkByQid(uacQidLink.getQid())).thenReturn(uacQidLink);

    UUID invalidCaseId = UUID.randomUUID();
    when(caseService.findByCaseId(invalidCaseId))
        .thenThrow(new CaseIdNotFoundException(invalidCaseId));

    QidLink qidLink = new QidLink();
    qidLink.setCaseId(invalidCaseId);
    qidLink.setQuestionnaireId(uacQidLink.getQid());

    // When, then
    mockMvc
        .perform(
            put("/qids/link")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(qidLink)))
        .andExpect(status().isNotFound())
        .andExpect(handler().handlerType(QidEndpoint.class));
  }
}
