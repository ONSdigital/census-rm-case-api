package uk.gov.ons.census.caseapisvc.endpoint;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        .perform(
            MockMvcRequestBuilders.post("/uacqid/create")
                .content("{\"questionnaireType\":\"1\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());
  }
}
