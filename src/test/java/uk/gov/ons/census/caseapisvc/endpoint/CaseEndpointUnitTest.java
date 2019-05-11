package uk.gov.ons.census.caseapisvc.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createMultipleCasesWithEvents;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createSingleCaseWithEvents;

import ma.glasnost.orika.MapperFacade;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.ons.census.caseapisvc.service.CaseService;
import uk.gov.ons.census.caseapisvc.utility.CaseSvcBeanMapper;

public class CaseEndpointUnitTest {

  private final String METHOD_NAME_FIND_CASE_BY_ID = "findCaseByCaseId";
  private final String METHOD_NAME_FIND_CASE_BY_REFERENCE = "findCaseByReference";
  private final String METHOD_NAME_FIND_CASES_BY_UPRN = "findCasesByUPRN";

  private String TEST1_CASE_ID = "2e083ab1-41f7-4dea-a3d9-77f48458b5ca";
  private String TEST1_CASE_REFERENCE_ID = "123";

  private String TEST2_CASE_ID = "3e948f6a-00bb-466d-88a7-b0990a827b53";

  private String TEST_UPRN = "123";

  private MockMvc mockMvc;

  @Mock private CaseService caseService;

  @Spy private MapperFacade mapperFacade = new CaseSvcBeanMapper();

  @InjectMocks private CaseEndpoint caseEndpoint;

  @Before
  public void setUp() {
    initMocks(this);

    this.mockMvc = MockMvcBuilders.standaloneSetup(caseEndpoint).build();
  }

  @Test
  public void getMultipleCasesWithEventsWhenSearchingByUPRN() throws Exception {
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
  public void getACaseWithoutEventsWhenSearchingByUPRN() throws Exception {
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
  public void getACaseWithoutEventsByDefaultWhenSearchingByUPRN() throws Exception {
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
  public void receiveNotFoundExceptionWhenUPRNDoesNotExist() {
    when(caseService.findByUPRN(any()))
        .thenThrow(new HttpClientErrorException(NOT_FOUND, "test message"));

    try {
      mockMvc.perform(
          get(createUrl("/cases/uprn/%s", TEST_UPRN)).accept(MediaType.APPLICATION_JSON));
    } catch (Exception e) {
      assertThat(((HttpClientErrorException) e.getCause()).getStatusCode()).isEqualTo(NOT_FOUND);
    }
  }

  @Test
  public void getACaseWithEventsWhenSearchingByCaseId() throws Exception {
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
  public void getACaseWithoutEventsWhenSearchingByCaseId() throws Exception {
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
        .andExpect(jsonPath("$.caseEvents", hasSize(0)));
  }

  @Test
  public void getACaseWithoutEventsByDefaultWhenSearchingByCaseId() throws Exception {
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
  public void receiveNotFoundExceptionWhenCaseDoesNotExist() {
    when(caseService.findByCaseId(any()))
        .thenThrow(new HttpClientErrorException(NOT_FOUND, "test message"));

    try {
      mockMvc.perform(
          get(createUrl("/cases/%s", TEST1_CASE_ID)).accept(MediaType.APPLICATION_JSON));
    } catch (Exception e) {
      assertThat(((HttpClientErrorException) e.getCause()).getStatusCode()).isEqualTo(NOT_FOUND);
    }
  }

  @Test
  public void getACaseWithEventsWhenSearchingByCaseReference() throws Exception {
    when(caseService.findByReference(any())).thenReturn(createSingleCaseWithEvents());

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
  public void getACaseWithoutEventsWhenSearchingByCaseReference() throws Exception {
    when(caseService.findByReference(any())).thenReturn(createSingleCaseWithEvents());

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
  public void getACaseWithoutEventsByDefaultWhenSearchingByCaseReference() throws Exception {
    when(caseService.findByReference(any())).thenReturn(createSingleCaseWithEvents());

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
  public void receiveNotFoundExceptionWhenCaseReferenceDoesNotExist() {
    when(caseService.findByReference(any()))
        .thenThrow(new HttpClientErrorException(NOT_FOUND, "test message"));

    try {
      mockMvc.perform(
          get(createUrl("/cases/ref/%s", TEST1_CASE_REFERENCE_ID))
              .accept(MediaType.APPLICATION_JSON));
    } catch (Exception e) {
      assertThat(((HttpClientErrorException) e.getCause()).getStatusCode()).isEqualTo(NOT_FOUND);
    }
  }

  private String createUrl(String urlFormat, String param1) {
    return String.format(urlFormat, param1);
  }
  // TODO test 400, 401 etc...
}
