package uk.gov.ons.census.casesvc.endpoint;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.census.casesvc.utility.DataUtils.create1TestCase;
import static uk.gov.ons.census.casesvc.utility.DataUtils.create3TestCases;
import static uk.gov.ons.ctp.common.MvcHelper.getJson;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;

import ma.glasnost.orika.MapperFacade;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ons.census.casesvc.service.CaseService;
import uk.gov.ons.census.casesvc.utility.CaseSvcBeanMapper;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.CTPException.Fault;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.common.jackson.CustomObjectMapper;

public class CaseEndpointUnitTest {

  private final String METHOD_NAME_FIND_CASE_BY_ID = "findCaseByCaseId";
  private final String METHOD_NAME_FIND_CASE_BY_REFERENCE = "findCaseByReference";
  private final String METHOD_NAME_FIND_CASES_BY_UPRN = "findCasesByUPRN";

  private String TEST1_CASE_ID = "2e083ab1-41f7-4dea-a3d9-77f48458b5ca";
  private String TEST1_CASE_REFERENCE_ID = "123";

  private String TEST2_CASE_ID = "3e948f6a-00bb-466d-88a7-b0990a827b53";

  private String TEST3_CASE_ID = "4ee74fac-e0fd-41ac-8322-414b8d5e978d";

  private String TEST_UPRN = "123";

  private MockMvc mockMvc;

  @Mock private CaseService caseService;

  @Spy private MapperFacade mapperFacade = new CaseSvcBeanMapper();

  @InjectMocks private CaseEndpoint caseEndpoint;

  @Before
  public void setUp() {
    initMocks(this);

    this.mockMvc =
        MockMvcBuilders.standaloneSetup(caseEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(new CustomObjectMapper()))
            .build();
  }

  @Test
  public void shouldReturnMultipleCasesWithEventsWhenSearchingByUPRN() throws Exception {

    when(caseService.findByUPRN(anyString())).thenReturn(create3TestCases());

    String url = String.format("/cases/uprn/%s?caseEvents=true", TEST_UPRN);

    mockMvc
        .perform(get(url).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASES_BY_UPRN))
        .andExpect(jsonPath("$[0].caseId", is(TEST1_CASE_ID)))
        .andExpect(jsonPath("$[0].caseEvents", hasSize(1)))
        .andExpect(jsonPath("$[1].caseId", is(TEST2_CASE_ID)))
        .andExpect(jsonPath("$[1].caseEvents", hasSize(1)))
        .andExpect(jsonPath("$[2].caseId", is(TEST3_CASE_ID)))
        .andExpect(jsonPath("$[2].caseEvents", hasSize(1)));
  }

  @Test
  public void shouldReturnACaseWithoutEventsWhenSearchingByUPRN() throws Exception {
    when(caseService.findByUPRN(anyString())).thenReturn(create3TestCases());

    String url = String.format("/cases/uprn/%s?caseEvents=false", TEST_UPRN);

    mockMvc
        .perform(get(url).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASES_BY_UPRN))
        .andExpect(jsonPath("$[0].caseId", is(TEST1_CASE_ID)))
        .andExpect(jsonPath("$[0].caseEvents", hasSize(0)))
        .andExpect(jsonPath("$[1].caseId", is(TEST2_CASE_ID)))
        .andExpect(jsonPath("$[1].caseEvents", hasSize(0)))
        .andExpect(jsonPath("$[2].caseId", is(TEST3_CASE_ID)))
        .andExpect(jsonPath("$[2].caseEvents", hasSize(0)));
  }

  @Test
  public void shouldReturnACaseWithoutEventsByDefaultWhenSearchingByUPRN() throws Exception {
    when(caseService.findByUPRN(anyString())).thenReturn(create3TestCases());

    String url = String.format("/cases/uprn/%s", TEST_UPRN);

    mockMvc
        .perform(get(url).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASES_BY_UPRN))
        .andExpect(jsonPath("$[0].caseId", is(TEST1_CASE_ID)))
        .andExpect(jsonPath("$[0].caseEvents", hasSize(0)))
        .andExpect(jsonPath("$[1].caseId", is(TEST2_CASE_ID)))
        .andExpect(jsonPath("$[1].caseEvents", hasSize(0)))
        .andExpect(jsonPath("$[2].caseId", is(TEST3_CASE_ID)))
        .andExpect(jsonPath("$[2].caseEvents", hasSize(0)));
  }

  @Test
  public void shouldThrowNotFoundExceptionWhenUPRNDoesNotExist() throws Exception {

    when(caseService.findByUPRN(anyString()))
        .thenThrow(new CTPException(Fault.RESOURCE_NOT_FOUND, "test message"));

    String url = String.format("/cases/uprn/%s", TEST_UPRN);

    mockMvc
        .perform(getJson(url).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASES_BY_UPRN))
        .andExpect(jsonPath("$.error.message", is("test message")));
  }

  @Test
  public void shouldReturnACaseWithEventsWhenSearchingByCaseId() throws Exception {
    when(caseService.findByCaseId(any())).thenReturn(create1TestCase());

    String url = String.format("/cases/%s?caseEvents=true", TEST1_CASE_ID);

    mockMvc
        .perform(get(url).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASE_BY_ID))
        .andExpect(jsonPath("$.caseId", is(TEST1_CASE_ID)))
        .andExpect(jsonPath("$.caseEvents", hasSize(1)));
  }

  @Test
  public void shouldReturnACaseWithoutEventsWhenSearchingByCaseId() throws Exception {
    when(caseService.findByCaseId(any())).thenReturn(create1TestCase());

    String url = String.format("/cases/%s?caseEvents=false", TEST1_CASE_ID);

    mockMvc
        .perform(get(url).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASE_BY_ID))
        .andExpect(jsonPath("$.caseId", is(TEST1_CASE_ID)))
        .andExpect(jsonPath("$.caseEvents", hasSize(0)));
  }

  @Test
  public void shouldReturnACaseWithoutEventsByDefaultWhenSearchingByCaseId() throws Exception {
    when(caseService.findByCaseId(any())).thenReturn(create1TestCase());

    String url = String.format("/cases/%s", TEST1_CASE_ID);

    mockMvc
        .perform(get(url).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASE_BY_ID))
        .andExpect(jsonPath("$.caseId", is(TEST1_CASE_ID)))
        .andExpect(jsonPath("$.caseEvents", hasSize(0)));
  }

  @Test
  public void shouldThrowNotFoundExceptionWhenCaseDoesNotExist() throws Exception {
    when(caseService.findByCaseId(any()))
        .thenThrow(new CTPException(Fault.RESOURCE_NOT_FOUND, "test message"));

    String url = String.format("/cases/%s", TEST1_CASE_ID);

    mockMvc
        .perform(getJson(url).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASE_BY_ID))
        .andExpect(jsonPath("$.error.message", is("test message")));
  }

  @Test
  public void shouldReturnACaseWithEventsWhenSearchingByCaseReference() throws Exception {

    when(caseService.findByReference(any())).thenReturn(create1TestCase());

    String url = String.format("/cases/ref/%s?caseEvents=true", TEST1_CASE_REFERENCE_ID);

    mockMvc
        .perform(get(url).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASE_BY_REFERENCE))
        .andExpect(jsonPath("$.caseRef", is(TEST1_CASE_REFERENCE_ID)))
        .andExpect(jsonPath("$.caseEvents", hasSize(1)));
  }

  @Test
  public void shouldReturnACaseWithoutEventsWhenSearchingByCaseReference() throws Exception {
    when(caseService.findByReference(any())).thenReturn(create1TestCase());

    String url = String.format("/cases/ref/%s?caseEvents=false", TEST1_CASE_REFERENCE_ID);

    mockMvc
        .perform(get(url).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASE_BY_REFERENCE))
        .andExpect(jsonPath("$.caseRef", is(TEST1_CASE_REFERENCE_ID)))
        .andExpect(jsonPath("$.caseEvents", hasSize(0)));
  }

  @Test
  public void shouldReturnACaseWithoutEventsByDefaultWhenSearchingByCaseReference()
      throws Exception {
    when(caseService.findByReference(any())).thenReturn(create1TestCase());

    String url = String.format("/cases/ref/%s", TEST1_CASE_REFERENCE_ID);

    mockMvc
        .perform(get(url).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASE_BY_REFERENCE))
        .andExpect(jsonPath("$.caseRef", is(TEST1_CASE_REFERENCE_ID)))
        .andExpect(jsonPath("$.caseEvents", hasSize(0)));
  }

  @Test
  public void shouldThrowNotFoundExceptionWhenCaseReferenceDoesNotExist() throws Exception {

    when(caseService.findByReference(any()))
        .thenThrow(new CTPException(Fault.RESOURCE_NOT_FOUND, "test message"));

    String url = String.format("/cases/ref/%s", TEST1_CASE_REFERENCE_ID);

    mockMvc
        .perform(getJson(url).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASE_BY_REFERENCE))
        .andExpect(jsonPath("$.error.message", is("test message")));
  }
}
