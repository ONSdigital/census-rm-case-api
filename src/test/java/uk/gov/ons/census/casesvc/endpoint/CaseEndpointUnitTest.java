package uk.gov.ons.census.casesvc.endpoint;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.MvcHelper.getJson;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;

import java.util.UUID;
import ma.glasnost.orika.MapperFacade;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ons.census.casesvc.model.entity.Case;
import uk.gov.ons.census.casesvc.service.CaseService;
import uk.gov.ons.census.casesvc.utility.CaseSvcBeanMapper;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.common.jackson.CustomObjectMapper;

public class CaseEndpointUnitTest {

  private static final String METHOD_NAME_FIND_CASE_BY_ID = "findByCaseId";

  private UUID TEST1_CASE_ID;

  @Mock private CaseService caseService;

  @Spy private MapperFacade mapperFacade = new CaseSvcBeanMapper();

  @InjectMocks private CaseEndpoint caseEndpoint;

  private MockMvc mockMvc;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    this.mockMvc =
        MockMvcBuilders.standaloneSetup(caseEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(new CustomObjectMapper()))
            .build();

    this.TEST1_CASE_ID = UUID.randomUUID();
  }

  @Test
  public void shouldReturnCaseWhenCaseIdExists() throws Exception {

    Case testCase = createTestCase();

    when(caseService.findByCaseId(anyObject())).thenReturn(testCase);

    ResultActions actions =
        mockMvc.perform(getJson(String.format("/cases/%s", TEST1_CASE_ID.toString())));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName(METHOD_NAME_FIND_CASE_BY_ID));
    actions.andExpect(jsonPath("$.caseId", is(TEST1_CASE_ID.toString())));
  }

  private Case createTestCase() {
    Case caze = new Case();

    caze.setCaseId(TEST1_CASE_ID);

    return caze;
  }
}