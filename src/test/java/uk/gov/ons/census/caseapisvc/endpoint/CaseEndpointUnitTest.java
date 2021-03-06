package uk.gov.ons.census.caseapisvc.endpoint;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.CREATED_UAC;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.TEST_CCS_QID;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.TEST_POSTCODE;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createCasesWithAddressInvalid;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createCcsUacQidLink;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createMultipleCasesWithEvents;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createSingleCaseWithEvents;
import static uk.gov.ons.census.caseapisvc.utility.DataUtils.createUacQidCreatedPayload;

import java.util.List;
import java.util.UUID;
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
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedPayloadDTO;
import uk.gov.ons.census.caseapisvc.model.entity.Case;
import uk.gov.ons.census.caseapisvc.model.entity.EventType;
import uk.gov.ons.census.caseapisvc.model.entity.UacQidLink;
import uk.gov.ons.census.caseapisvc.service.CaseService;
import uk.gov.ons.census.caseapisvc.service.UacQidService;
import uk.gov.ons.census.caseapisvc.utility.DataUtils;

public class CaseEndpointUnitTest {

  private static final String METHOD_NAME_FIND_CASE_BY_ID = "findCaseByCaseId";
  private static final String METHOD_NAME_FIND_CASE_BY_REFERENCE = "findCaseByReference";
  private static final String METHOD_NAME_FIND_CASES_BY_UPRN = "findCasesByUPRN";

  private static final String TEST1_CASE_ID = "2e083ab1-41f7-4dea-a3d9-77f48458b5ca";
  private static final String TEST1_CASE_REFERENCE_ID = "1234567890";

  private static final String TEST2_CASE_ID = "3e948f6a-00bb-466d-88a7-b0990a827b53";
  private static final String RM_TELEPHONE_CAPTURE_HOUSEHOLD_INDIVIDUAL = "RM_TC_HI";
  private static final String RM_TELEPHONE_CAPTURE = "RM_TC";

  private static final String TEST_UPRN = "123";
  public static final String TEST_QID = "test_qid";

  private MockMvc mockMvc;

  @Mock private CaseService caseService;
  @Mock private UacQidService uacQidService;

  @Spy
  private MapperFacade mapperFacade = new DefaultMapperFactory.Builder().build().getMapperFacade();

  @InjectMocks private CaseEndpoint caseEndpoint;

  @Before
  public void setUp() {
    initMocks(this);

    mockMvc = MockMvcBuilders.standaloneSetup(caseEndpoint).build();
  }

  @After
  public void tearDown() {
    reset(caseService);
  }

  @Test
  public void getMultipleCasesWithEventsByUPRN() throws Exception {
    when(caseService.findByUPRN(anyString(), eq(false)))
        .thenReturn(createMultipleCasesWithEvents());

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/uprn/%s", TEST_UPRN))
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
    when(caseService.findByUPRN(anyString(), eq(false)))
        .thenReturn(createMultipleCasesWithEvents());

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/uprn/%s", TEST_UPRN))
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
    when(caseService.findByUPRN(anyString(), eq(false)))
        .thenReturn(createMultipleCasesWithEvents());

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/uprn/%s", TEST_UPRN))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASES_BY_UPRN))
        .andExpect(jsonPath("$[0].id", is(TEST1_CASE_ID)))
        .andExpect(jsonPath("$[0].caseEvents", hasSize(0)))
        .andExpect(jsonPath("$[1].id", is(TEST2_CASE_ID)))
        .andExpect(jsonPath("$[1].caseEvents", hasSize(0)));
  }

  @Test
  public void receiveNotFoundExceptionWhenUPRNDoesNotExist() throws Exception {
    when(caseService.findByUPRN(any(), eq(false))).thenThrow(new UPRNNotFoundException("a uprn"));

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/uprn/%s", TEST_UPRN))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void getMultipleCasesWithAddressInvalidByUPRN() throws Exception {
    when(caseService.findByUPRN(anyString(), eq(false)))
        .thenReturn(createCasesWithAddressInvalid());

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/uprn/%s", TEST_UPRN))
                .param("validAddressOnly", "false")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASES_BY_UPRN))
        .andExpect(jsonPath("$[0].id", is(TEST1_CASE_ID)))
        .andExpect(jsonPath("$[0].addressInvalid", is(false)))
        .andExpect(jsonPath("$[1].id", is(TEST2_CASE_ID)))
        .andExpect(jsonPath("$[1].addressInvalid", is(true)));
  }

  @Test
  public void getOnlyCasesWithValidAddressByUPRN() throws Exception {
    when(caseService.findByUPRN(anyString(), eq(true))).thenReturn(createCasesWithAddressInvalid());

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/uprn/%s", TEST_UPRN))
                .param("validAddressOnly", "true")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASES_BY_UPRN))
        .andExpect(jsonPath("$[0].id", is(TEST1_CASE_ID)))
        .andExpect(jsonPath("$[0].addressInvalid", is(false)));
  }

  @Test
  public void getACaseWithEventsByCaseId() throws Exception {
    when(caseService.findByCaseId(any())).thenReturn(createSingleCaseWithEvents());

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/%s", TEST1_CASE_ID))
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
            get(DataUtils.createUrl("/cases/%s", TEST1_CASE_ID))
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
            get(DataUtils.createUrl("/cases/%s", TEST1_CASE_ID))
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
        .perform(
            get(DataUtils.createUrl("/cases/%s", TEST1_CASE_ID)).accept(MediaType.APPLICATION_JSON))
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
        .perform(
            get(DataUtils.createUrl("/cases/qid/%s", TEST_QID)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(jsonPath("$.id", is(TEST1_CASE_ID)));
  }

  @Test
  public void receiveNotFoundExceptionWhenCaseIdDoesNotExist() throws Exception {
    when(caseService.findByCaseId(any())).thenThrow(new CaseIdNotFoundException(UUID.randomUUID()));

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/%s", TEST1_CASE_ID)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void getACaseWithEventsByCaseReference() throws Exception {
    when(caseService.findByReference(anyLong())).thenReturn(createSingleCaseWithEvents());

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/ref/%s", TEST1_CASE_REFERENCE_ID))
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
    when(caseService.findByReference(anyLong())).thenReturn(createSingleCaseWithEvents());

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/ref/%s", TEST1_CASE_REFERENCE_ID))
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
    when(caseService.findByReference(anyLong())).thenReturn(createSingleCaseWithEvents());

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/ref/%s", TEST1_CASE_REFERENCE_ID))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName(METHOD_NAME_FIND_CASE_BY_REFERENCE))
        .andExpect(jsonPath("$.caseRef", is(TEST1_CASE_REFERENCE_ID)))
        .andExpect(jsonPath("$.caseEvents", hasSize(0)));
  }

  @Test
  public void receiveNotFoundExceptionWhenCaseReferenceDoesNotExist() throws Exception {
    when(caseService.findByReference(anyLong())).thenThrow(new CaseReferenceNotFoundException(0));

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/ref/%s", TEST1_CASE_REFERENCE_ID))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void getCcsQidByCaseId() throws Exception {
    UacQidLink ccsUacQidLink = createCcsUacQidLink(TEST_CCS_QID, true);
    when(caseService.findCCSUacQidLinkByCaseId(any())).thenReturn(ccsUacQidLink);

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/ccs/%s/qid", TEST1_CASE_ID))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(jsonPath("$.questionnaireId", is(TEST_CCS_QID)))
        .andExpect(jsonPath("$.active", is(true)))
        .andExpect(jsonPath("$.formType", is("H")));
  }

  @Test
  public void getInactiveCcsQidByCaseId() throws Exception {
    UacQidLink ccsUacQidLink = createCcsUacQidLink(TEST_CCS_QID, false);
    when(caseService.findCCSUacQidLinkByCaseId(any())).thenReturn(ccsUacQidLink);

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/ccs/%s/qid", TEST1_CASE_ID))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(jsonPath("$.questionnaireId", is(TEST_CCS_QID)))
        .andExpect(jsonPath("$.active", is(false)))
        .andExpect(jsonPath("$.formType", is("H")));
  }

  @Test
  public void getCcsQidByCaseIdCcsCaseNotFound() throws Exception {
    when(caseService.findCCSUacQidLinkByCaseId(any()))
        .thenThrow(new CaseIdNotFoundException(UUID.randomUUID()));

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/ccs/%s/qid", UUID.randomUUID().toString()))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void getCcsQidByCaseIdCcsQIDNotFound() throws Exception {
    when(caseService.findCCSUacQidLinkByCaseId(any())).thenThrow(new QidNotFoundException("test"));

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/ccs/%s/qid", UUID.randomUUID().toString()))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void getNewUacQidByCaseId() throws Exception {
    Case caze = createSingleCaseWithEvents();
    caze.setTreatmentCode("HH_XXXXXE");
    caze.setCaseType("HH");
    caze.setRegion("E1000");
    UacQidCreatedPayloadDTO uacQidCreated = createUacQidCreatedPayload(TEST_QID, caze.getCaseId());
    when(caseService.findByCaseId(eq(caze.getCaseId()))).thenReturn(caze);
    when(uacQidService.createAndLinkUacQid(eq(caze.getCaseId()), eq(1))).thenReturn(uacQidCreated);

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/%s/qid", caze.getCaseId().toString()))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(jsonPath("$.questionnaireId", is(TEST_QID)))
        .andExpect(jsonPath("$.uac", is(CREATED_UAC)))
        .andExpect(jsonPath("$.formType", is("H")))
        .andExpect(jsonPath("$.questionnaireType", is("01")));

    verify(uacQidService).createAndLinkUacQid(eq(caze.getCaseId()), anyInt());
    verify(caseService)
        .buildAndSendTelephoneCaptureFulfilmentRequest(
            eq(caze.getCaseId()), eq(RM_TELEPHONE_CAPTURE), isNull(), eq(uacQidCreated));
  }

  @Test
  public void getNewUacQidByCaseIdNotFound() throws Exception {
    when(caseService.findByCaseId(any())).thenThrow(new CaseIdNotFoundException(UUID.randomUUID()));

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/%s/qid", TEST1_CASE_ID))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void getNewIndividualUacQidByCaseId() throws Exception {
    Case parentCase = createSingleCaseWithEvents();
    UUID individualCaseId = UUID.randomUUID();
    parentCase.setTreatmentCode("HH_XXXXXE");
    parentCase.setCaseType("HH");
    parentCase.setRegion("E1000");
    UacQidCreatedPayloadDTO uacQidCreated = createUacQidCreatedPayload(TEST_QID, individualCaseId);
    when(caseService.findByCaseId(eq(parentCase.getCaseId()))).thenReturn(parentCase);
    when(uacQidService.createAndLinkUacQid(eq(individualCaseId), eq(21))).thenReturn(uacQidCreated);
    when(caseService.caseExistsByCaseId(eq(individualCaseId))).thenReturn(false);

    mockMvc
        .perform(
            get(String.format(
                    "/cases/%s/qid?individual=true&individualCaseId=%s",
                    parentCase.getCaseId(), individualCaseId))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(jsonPath("$.questionnaireId", is(TEST_QID)))
        .andExpect(jsonPath("$.uac", is(CREATED_UAC)))
        .andExpect(jsonPath("$.formType", is("I")))
        .andExpect(jsonPath("$.questionnaireType", is("21")));

    verify(caseService)
        .buildAndSendTelephoneCaptureFulfilmentRequest(
            eq(parentCase.getCaseId()),
            eq(RM_TELEPHONE_CAPTURE_HOUSEHOLD_INDIVIDUAL),
            eq(individualCaseId),
            eq(uacQidCreated));
  }

  @Test
  public void getNewIndividualUacQidButIndividualCaseExists() throws Exception {
    Case parentCase = createSingleCaseWithEvents();
    UUID individualCaseId = UUID.randomUUID();
    parentCase.setTreatmentCode("HH_XXXXXE");
    when(caseService.findByCaseId(eq(parentCase.getCaseId()))).thenReturn(parentCase);
    when(caseService.caseExistsByCaseId(eq(individualCaseId))).thenReturn(true);

    mockMvc
        .perform(
            get(String.format(
                    "/cases/%s/qid?individual=true&individualCaseId=%s",
                    parentCase.getCaseId(), individualCaseId))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(handler().handlerType(CaseEndpoint.class));

    verify(caseService, never())
        .buildAndSendTelephoneCaptureFulfilmentRequest(any(), any(), any(), any());
    verifyZeroInteractions(uacQidService);
  }

  @Test
  public void getNewIndividualUacQidButIndividualParamNotGiven() throws Exception {
    Case parentCase = createSingleCaseWithEvents();
    when(caseService.findByCaseId(any())).thenReturn(parentCase);
    String individualCaseId = UUID.randomUUID().toString();

    mockMvc
        .perform(
            get(String.format(
                    "/cases/%s/qid?individualCaseId=%s", parentCase.getCaseId(), individualCaseId))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(handler().handlerType(CaseEndpoint.class));

    verifyZeroInteractions(uacQidService);
  }

  @Test
  public void getIndividualQidForSpgUnitLevelFails404IfIndividualCaseIdSet() throws Exception {
    Case caze = createSingleCaseWithEvents();
    caze.setCaseType("SPG");
    caze.setAddressLevel("U");
    UUID individualCaseId = UUID.randomUUID();
    when(caseService.findByCaseId(eq(caze.getCaseId()))).thenReturn(caze);
    when(caseService.caseExistsByCaseId(eq(individualCaseId))).thenReturn(false);

    mockMvc
        .perform(
            get(String.format(
                    "/cases/%s/qid?individual=true&individualCaseId=%s",
                    caze.getCaseId(), individualCaseId))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(handler().handlerType(CaseEndpoint.class));

    verify(caseService).findByCaseId(caze.getCaseId());
  }

  @Test
  public void getIndividualResponseForSpgUnitCase() throws Exception {
    Case caze = createSingleCaseWithEvents();
    caze.setCaseType("SPG");
    caze.setAddressLevel("U");
    caze.setRegion("E");
    caze.setTreatmentCode("SPG_XXXXXE");
    UacQidCreatedPayloadDTO uacQidCreated = createUacQidCreatedPayload(TEST_QID, caze.getCaseId());
    when(caseService.findByCaseId(eq(caze.getCaseId()))).thenReturn(caze);
    when(uacQidService.createAndLinkUacQid(eq(caze.getCaseId()), eq(21))).thenReturn(uacQidCreated);

    mockMvc
        .perform(
            get(String.format("/cases/%s/qid?individual=true", caze.getCaseId()))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(jsonPath("$.questionnaireId", is(TEST_QID)))
        .andExpect(jsonPath("$.uac", is(CREATED_UAC)))
        .andExpect(jsonPath("$.formType", is("I")))
        .andExpect(jsonPath("$.questionnaireType", is("21")));

    verify(caseService, never()).caseExistsByCaseId(caze.getCaseId());
  }

  @Test
  public void getIndividualResponseForSpgEstabCase() throws Exception {
    Case caze = createSingleCaseWithEvents();
    caze.setCaseType("SPG");
    caze.setAddressLevel("E");
    caze.setRegion("E");
    caze.setTreatmentCode("SPG_XXXXXE");
    UacQidCreatedPayloadDTO uacQidCreated = createUacQidCreatedPayload(TEST_QID, caze.getCaseId());
    when(caseService.findByCaseId(eq(caze.getCaseId()))).thenReturn(caze);
    when(uacQidService.createAndLinkUacQid(eq(caze.getCaseId()), eq(21))).thenReturn(uacQidCreated);

    mockMvc
        .perform(
            get(String.format("/cases/%s/qid?individual=true", caze.getCaseId()))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(jsonPath("$.questionnaireId", is(TEST_QID)))
        .andExpect(jsonPath("$.uac", is(CREATED_UAC)))
        .andExpect(jsonPath("$.formType", is("I")))
        .andExpect(jsonPath("$.questionnaireType", is("21")));

    verify(caseService, never()).caseExistsByCaseId(caze.getCaseId());
  }

  @Test
  public void getIndividualResponseForCeEstabCase() throws Exception {
    Case caze = createSingleCaseWithEvents();
    caze.setCaseType("CE");
    caze.setRegion("E");
    caze.setAddressLevel("E");
    caze.setTreatmentCode("CE_XXXXXE");
    UacQidCreatedPayloadDTO uacQidCreated = createUacQidCreatedPayload(TEST_QID, caze.getCaseId());
    when(caseService.findByCaseId(eq(caze.getCaseId()))).thenReturn(caze);
    when(uacQidService.createAndLinkUacQid(eq(caze.getCaseId()), eq(21))).thenReturn(uacQidCreated);

    mockMvc
        .perform(
            get(String.format("/cases/%s/qid?individual=true", caze.getCaseId()))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(jsonPath("$.questionnaireId", is(TEST_QID)))
        .andExpect(jsonPath("$.uac", is(CREATED_UAC)))
        .andExpect(jsonPath("$.formType", is("I")))
        .andExpect(jsonPath("$.questionnaireType", is("21")));

    verify(caseService, never()).caseExistsByCaseId(caze.getCaseId());
  }

  @Test
  public void getCCSCasesByPostcode() throws Exception {
    Case ccsCase = createSingleCaseWithEvents();
    ccsCase.setSurvey("CCS");
    ccsCase.setPostcode("AB12BC");
    when(caseService.findCCSCasesByPostcode(anyString())).thenReturn(List.of(ccsCase));

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/ccs/postcode/%s", ccsCase.getPostcode()))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(ccsCase.getCaseId().toString())))
        .andExpect(jsonPath("$[0].postcode", is(ccsCase.getPostcode())));
  }

  @Test
  public void getCasesByPostcode() throws Exception {
    Case caze = createSingleCaseWithEvents();
    when(caseService.findByPostcode(TEST_POSTCODE)).thenReturn(List.of((caze)));

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/postcode/%s", TEST_POSTCODE))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(caze.getCaseId().toString())))
        .andExpect(jsonPath("$[0].postcode", is(caze.getPostcode())));
  }

  @Test
  public void getAllCaseDetails() throws Exception {
    Case caze = createSingleCaseWithEvents();
    when(caseService.findByCaseId(UUID.fromString(TEST1_CASE_ID))).thenReturn(caze);

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/case-details/%s", TEST1_CASE_ID))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(jsonPath("$.id", is(caze.getCaseId().toString())))
        .andExpect(jsonPath("$.events", hasSize(1)));
  }

  @Test
  public void rmUacCreatedEventNotPresent() throws Exception {
    Case caze = createSingleCaseWithEvents();
    caze.getUacQidLinks().get(0).getEvents().get(0).setEventType(EventType.RM_UAC_CREATED);
    when(caseService.findByCaseId(UUID.fromString(TEST1_CASE_ID))).thenReturn(caze);

    mockMvc
        .perform(
            get(DataUtils.createUrl("/cases/case-details/%s", TEST1_CASE_ID))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(jsonPath("$.id", is(caze.getCaseId().toString())))
        .andExpect(jsonPath("$.events", hasSize(0)));
  }
}
