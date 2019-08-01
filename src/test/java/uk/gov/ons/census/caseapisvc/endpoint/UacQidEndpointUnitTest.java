package uk.gov.ons.census.caseapisvc.endpoint;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ons.census.caseapisvc.client.UacQidServiceClient;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidDTO;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UacQidEndpointUnitTest {

  private static final String CREATE_UACQID_PAIR = "uacqid/create";

  private MockMvc mockMvc;

  @Mock private UacQidServiceClient uacQidServiceClient;
  
  @InjectMocks private UacQidEndpoint uacQidEndpoint;

  @Before
  public void setUp() {
    initMocks(this);

    this.mockMvc = MockMvcBuilders.standaloneSetup(uacQidEndpoint).build();
  }

  @After
  public void tearDown() {
    reset(uacQidServiceClient);
  }

  @Test
  public void createUacQidPair() throws Exception {
    UacQidDTO uacQidDto = new UacQidDTO();
    uacQidDto.setQid("0220000000005700");
    uacQidDto.setUac("6ghnj22s5bp8r6rd");
    when(uacQidServiceClient.generateUacQid(1)).thenReturn(uacQidDto);

    mockMvc
        .perform(MockMvcRequestBuilders.post("/uacqid/create")
                .content("{\"id\":\"1234\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
//        .andExpect(handler().handlerType(CaseEndpoint.class))
//        .andExpect(handler().methodName(METHOD_NAME_FIND_CASES_BY_UPRN))
//        .andExpect(jsonPath("$[0].id", is(TEST1_CASE_ID)))
//        .andExpect(jsonPath("$[0].caseEvents", hasSize(1)))
//        .andExpect(jsonPath("$[1].id", is(TEST2_CASE_ID)))
//        .andExpect(jsonPath("$[1].caseEvents", hasSize(1)));
  }

}
