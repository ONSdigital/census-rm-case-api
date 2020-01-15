package uk.gov.ons.census.caseapisvc.endpoint;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedPayloadDTO;
import uk.gov.ons.census.caseapisvc.service.UacQidService;

public class UacQidEndpointUnitTest {

  private static final String CREATE_UACQID_PAIR = "/uacqid/create";

  private MockMvc mockMvc;

  @Mock private UacQidService uacQidService;

  @InjectMocks private UacQidEndpoint uacQidEndpoint;

  @Before
  public void setUp() {
    initMocks(this);

    this.mockMvc = MockMvcBuilders.standaloneSetup(uacQidEndpoint).build();
  }

  @Test
  public void createUacQidPair() throws Exception {
    UUID caseId = UUID.randomUUID();
    UacQidCreatedPayloadDTO uacQidCreatedPayloadDTO = new UacQidCreatedPayloadDTO();
    uacQidCreatedPayloadDTO.setQid("0120000000005700");
    uacQidCreatedPayloadDTO.setUac("6ghnj22s5bp8r6rd");
    uacQidCreatedPayloadDTO.setCaseId(caseId.toString());
    when(uacQidService.createAndLinkUacQid(caseId.toString(), 1))
        .thenReturn(uacQidCreatedPayloadDTO);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(CREATE_UACQID_PAIR)
                .content(
                    String.format(
                        "{\"questionnaireType\":\"1\", \"caseId\":\"%s\"}", caseId.toString()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.uac").value(uacQidCreatedPayloadDTO.getUac()))
        .andExpect(jsonPath("$.qid").value(uacQidCreatedPayloadDTO.getQid()))
        .andExpect(jsonPath("$.caseId").value(uacQidCreatedPayloadDTO.getCaseId()));
  }
}
