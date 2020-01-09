package uk.gov.ons.census.caseapisvc.endpoint;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.TEST_CCS_QID;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createCcsUacQidLink;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createMultipleCasesWithEvents;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createSingleCaseWithEvents;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ons.census.caseapisvc.exception.CaseIdNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.CaseReferenceNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.QidNotFoundException;
import uk.gov.ons.census.caseapisvc.exception.UPRNNotFoundException;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.model.entity.UacQidLink;
import uk.gov.ons.census.caseapisvc.service.CaseService;

public class CaseEndpointUnitTest {

  private static final String METHOD_NAME_FIND_CASE_BY_ID = "findCaseByCaseId";
  private static final String METHOD_NAME_FIND_CASE_BY_REFERENCE = "findCaseByReference";
  private static final String METHOD_NAME_FIND_CASES_BY_UPRN = "findCasesByUPRN";

  private static final String TEST1_CASE_ID = "2e083ab1-41f7-4dea-a3d9-77f48458b5ca";
  private static final String TEST1_CASE_REFERENCE_ID = "123";

  private static final String TEST2_CASE_ID = "3e948f6a-00bb-466d-88a7-b0990a827b53";

  private static final String TEST_UPRN = "123";
  public static final String TEST_QID = "test_qid";

  private MockMvc mockMvc;

  @Mock private CaseService caseService;

  @Spy
  private MapperFacade mapperFacade = new DefaultMapperFactory.Builder().build().getMapperFacade();

  @InjectMocks private CaseEndpoint caseEndpoint;

  @Before
  public void setUp() {
    initMocks(this);

    this.mockMvc = MockMvcBuilders.standaloneSetup(caseEndpoint).build();
  }

  @After
  public void tearDown() {
    reset(caseService);
  }

  @Test
  public void getMultipleCasesWithEventsByUPRN() throws Exception {
    when(caseService.findByUPRN(anyString())).thenReturn(createMultipleCasesWithEvents());

    mockMvc
        .perform(
            get(createUrl("/cases/uprn/%s", TEST_UPRN))
                .param("caseEvents", "true")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASES_BY_UPRN))
        .andExpect(jsonPath("$[0].id", is(TEST1_CASE_ID)))
        .andExpect(jsonPath("$[0].caseEvents", hasSize(1)))
        .andExpect(jsonPath("$[1].id", is(TEST2_CASE_ID)))
        .andExpect(jsonPath("$[1].caseEvents", hasSize(1)));
  }

  @Test
  public void getMultipleCasesWithoutEventsByUPRN() throws Exception {
    when(caseService.findByUPRN(anyString())).thenReturn(createMultipleCasesWithEvents());

    mockMvc
        .perform(
            get(createUrl("/cases/uprn/%s", TEST_UPRN))
                .param("caseEvents", "false")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASES_BY_UPRN))
        .andExpect(jsonPath("$[0].id", is(TEST1_CASE_ID)))
        .andExpect(jsonPath("$[0].caseEvents", hasSize(0)))
        .andExpect(jsonPath("$[1].id", is(TEST2_CASE_ID)))
        .andExpect(jsonPath("$[1].caseEvents", hasSize(0)));
  }

  @Test
  public void getMultipleCasesWithoutEventsByDefaultByUPRN() throws Exception {
    when(caseService.findByUPRN(anyString())).thenReturn(createMultipleCasesWithEvents());

    mockMvc
        .perform(get(createUrl("/cases/uprn/%s", TEST_UPRN)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASES_BY_UPRN))
        .andExpect(jsonPath("$[0].id", is(TEST1_CASE_ID)))
        .andExpect(jsonPath("$[0].caseEvents", hasSize(0)))
        .andExpect(jsonPath("$[1].id", is(TEST2_CASE_ID)))
        .andExpect(jsonPath("$[1].caseEvents", hasSize(0)));
  }

  @Test
  public void receiveNotFoundExceptionWhenUPRNDoesNotExist() throws Exception {
    when(caseService.findByUPRN(any())).thenThrow(new UPRNNotFoundException("a uprn"));

    mockMvc
        .perform(get(createUrl("/cases/uprn/%s", TEST_UPRN)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void getACaseWithEventsByCaseId() throws Exception {
    when(caseService.findByCaseId(any())).thenReturn(createSingleCaseWithEvents());

    mockMvc
        .perform(
            get(createUrl("/cases/%s", TEST1_CASE_ID))
                .param("caseEvents", "true")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASE_BY_ID))
        .andExpect(jsonPath("$.id", is(TEST1_CASE_ID)))
        .andExpect(jsonPath("$.caseEvents", hasSize(1)));
  }

  @Test
  public void getACaseWithoutEventsByCaseId() throws Exception {
    when(caseService.findByCaseId(any())).thenReturn(createSingleCaseWithEvents());

    mockMvc
        .perform(
            get(createUrl("/cases/%s", TEST1_CASE_ID))
                .param("caseEvents", "false")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASE_BY_ID))
        .andExpect(jsonPath("$.id", is(TEST1_CASE_ID)))
        .andExpect(jsonPath("$.caseEvents", hasSize(0)))
        .andExpect(jsonPath("$.surveyType", is("CENSUS")));
  }

  @Test
  public void getACaseWithoutEventsByCaseIdForCCSCase() throws Exception {
    Case testCase = createSingleCaseWithEvents();
    testCase.setSurvey("CCS");
    when(caseService.findByCaseId(any())).thenReturn(testCase);

    mockMvc
        .perform(
            get(createUrl("/cases/%s", TEST1_CASE_ID))
                .param("caseEvents", "false")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASE_BY_ID))
        .andExpect(jsonPath("$.id", is(TEST1_CASE_ID)))
        .andExpect(jsonPath("$.caseEvents", hasSize(0)))
        .andExpect(jsonPath("$.surveyType", is("CCS")));
  }

  @Test
  public void getACaseWithoutEventsByDefaultByCaseId() throws Exception {
    when(caseService.findByCaseId(any())).thenReturn(createSingleCaseWithEvents());

    mockMvc
        .perform(get(createUrl("/cases/%s", TEST1_CASE_ID)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASE_BY_ID))
        .andExpect(jsonPath("$.id", is(TEST1_CASE_ID)))
        .andExpect(jsonPath("$.caseEvents", hasSize(0)));
  }

  @Test
  public void getCaseFromQidId() throws Exception {
    when(caseService.findCaseByQid(TEST_QID)).thenReturn(createSingleCaseWithEvents());

    mockMvc
        .perform(get(createUrl("/cases/qid/%s", TEST_QID)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(jsonPath("$.id", is(TEST1_CASE_ID)));
  }

  @Test
  public void receiveNotFoundExceptionWhenCaseIdDoesNotExist() throws Exception {
    when(caseService.findByCaseId(any())).thenThrow(new CaseIdNotFoundException("a case id"));

    mockMvc
        .perform(get(createUrl("/cases/%s", TEST1_CASE_ID)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void getACaseWithEventsByCaseReference() throws Exception {
    when(caseService.findByReference(anyInt())).thenReturn(createSingleCaseWithEvents());

    mockMvc
        .perform(
            get(createUrl("/cases/ref/%s", TEST1_CASE_REFERENCE_ID))
                .param("caseEvents", "true")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASE_BY_REFERENCE))
        .andExpect(jsonPath("$.caseRef", is(TEST1_CASE_REFERENCE_ID)))
        .andExpect(jsonPath("$.caseEvents", hasSize(1)));
  }

  @Test
  public void getACaseWithoutEventsByCaseReference() throws Exception {
    when(caseService.findByReference(anyInt())).thenReturn(createSingleCaseWithEvents());

    mockMvc
        .perform(
            get(createUrl("/cases/ref/%s", TEST1_CASE_REFERENCE_ID))
                .param("caseEvents", "false")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASE_BY_REFERENCE))
        .andExpect(jsonPath("$.caseRef", is(TEST1_CASE_REFERENCE_ID)))
        .andExpect(jsonPath("$.caseEvents", hasSize(0)));
  }

  @Test
  public void getACaseWithoutEventsByDefaultByCaseReference() throws Exception {
    when(caseService.findByReference(anyInt())).thenReturn(createSingleCaseWithEvents());

    mockMvc
        .perform(
            get(createUrl("/cases/ref/%s", TEST1_CASE_REFERENCE_ID))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASE_BY_REFERENCE))
        .andExpect(jsonPath("$.caseRef", is(TEST1_CASE_REFERENCE_ID)))
        .andExpect(jsonPath("$.caseEvents", hasSize(0)));
  }

  @Test
  public void receiveNotFoundExceptionWhenCaseReferenceDoesNotExist() throws Exception {
    when(caseService.findByReference(anyInt())).thenThrow(new CaseReferenceNotFoundException(0));

    mockMvc
        .perform(
            get(createUrl("/cases/ref/%s", TEST1_CASE_REFERENCE_ID))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void getCcsQidByCaseId() throws Exception {
    UacQidLink ccsUacQidLink = createCcsUacQidLink(TEST_CCS_QID, true);
    when(caseService.findCCSUacQidLinkByCaseId(any())).thenReturn(ccsUacQidLink);

    mockMvc
        .perform(
            get(createUrl("/cases/ccs/%s/qid", TEST1_CASE_ID)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(jsonPath("$.questionnaireId", is(TEST_CCS_QID)))
        .andExpect(jsonPath("$.active", is(true)));
  }

  @Test
  public void getInactiveCcsQidByCaseId() throws Exception {
    UacQidLink ccsUacQidLink = createCcsUacQidLink(TEST_CCS_QID, false);
    when(caseService.findCCSUacQidLinkByCaseId(any())).thenReturn(ccsUacQidLink);

    mockMvc
        .perform(
            get(createUrl("/cases/ccs/%s/qid", TEST1_CASE_ID)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(jsonPath("$.questionnaireId", is(TEST_CCS_QID)))
        .andExpect(jsonPath("$.active", is(false)));
  }

  @Test
  public void getCcsQidByCaseIdCcsCaseNotFound() throws Exception {
    when(caseService.findCCSUacQidLinkByCaseId(any())).thenThrow(new CaseIdNotFoundException("test"));

    mockMvc
        .perform(
            get(createUrl("/cases/ccs/%s/qid", TEST1_CASE_REFERENCE_ID))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void getCcsQidByCaseIdCcsQIDNotFound() throws Exception {
    when(caseService.findCCSUacQidLinkByCaseId(any())).thenThrow(new QidNotFoundException("test"));

    mockMvc
        .perform(
            get(createUrl("/cases/ccs/%s/qid", TEST1_CASE_REFERENCE_ID))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  private String createUrl(String urlFormat, String param1) {
    return String.format(urlFormat, param1);
  }
}
