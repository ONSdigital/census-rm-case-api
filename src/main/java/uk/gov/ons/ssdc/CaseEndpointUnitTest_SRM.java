// package uk.gov.ons.ssdc;
//
// import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
// import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
// import static org.hamcrest.core.Is.is;
// import static org.mockito.Mockito.*;
// import static org.mockito.MockitoAnnotations.initMocks;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// import static uk.gov.ons.census.caseapisvc.testutils.DataUtils.*;
// import static uk.gov.ons.census.caseapisvc.testutils.DataUtils.createSingleCaseWithEvents;
//
// import java.util.*;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.MvcResult;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
// import org.springframework.web.server.ResponseStatusException;
// import uk.gov.ons.census.caseapisvc.endpoint.CaseEndpoint;
// import uk.gov.ons.census.caseapisvc.model.dto.CaseContainerDTO;
// import uk.gov.ons.census.caseapisvc.service.CaseService;
// import uk.gov.ons.census.caseapisvc.testutils.DataUtils;
// import uk.gov.ons.census.common.model.entity.Case;
//
// public class CaseEndpointUnitTest_SRM {
//
//  private static final String METHOD_NAME_FIND_CASE_BY_ID = "findCaseById";
//  private static final String METHOD_NAME_FIND_CASE_BY_REFERENCE = "findCaseByReference";
//  private static final String METHOD_NAME_FIND_CASES_BY_SAMPLE_FIELDS = "findCasesBySampleFields";
//
//  private static final String TEST1_CASE_ID = "2e083ab1-41f7-4dea-a3d9-77f48458b5ca";
//  private static final String TEST1_CASE_REF = "1234567890";
//
//  private static final String TEST2_CASE_ID = "3e948f6a-00bb-466d-88a7-b0990a827b53";
//
//  private static final String TEST_UPRN = "123";
//  public static final String TEST_POSTCODE = "AB1 2BC";
//
//  private static final String URL_FOR_SEARCH_BY_FIELD_ENDPOINT = "/cases/searchByField";
//  private static final String UPRN_FIELD_NAME = "uprn";
//  private static final String POSTCODE_FIELD_NAME = "postcode";
//
//  private MockMvc mockMvc;
//
//  @Mock private CaseService caseService;
//
//  @InjectMocks private CaseEndpoint caseEndpoint;
//
//  @BeforeEach
//  public void setUp() {
//    MockitoAnnotations.initMocks(this);
//
//    mockMvc = MockMvcBuilders.standaloneSetup(caseEndpoint).build();
//  }
//
//  @AfterEach
//  public void tearDown() {
//    Mockito.reset(caseService);
//  }
//
//  @Test
//  public void getCaseReturnsExpectedCaseFields() throws Exception {
//    // Given
//    Case actualCase = DataUtils.createSingleCaseWithEvents();
//    Mockito.when(caseService.findById(ArgumentMatchers.any())).thenReturn(actualCase);
//
//    // When
//    MvcResult result =
//        mockMvc
//            .perform(
//                MockMvcRequestBuilders.get(DataUtils.createUrl("/cases/%s", TEST1_CASE_ID))
//                    .accept(MediaType.APPLICATION_JSON))
//            .andExpect(MockMvcResultMatchers.handler().handlerType(CaseEndpoint.class))
//            .andExpect(MockMvcResultMatchers.handler().methodName(METHOD_NAME_FIND_CASE_BY_ID))
//            .andReturn();
//
//    // Then
//    CaseContainerDTO responseCaseDTO =
//        DataUtils.mapper.readValue(result.getResponse().getContentAsString(),
// CaseContainerDTO.class);
//    Case responseCase = new Case();
//    responseCase.setCaseRef(Long.parseLong(responseCaseDTO.getCaseRef()));
//    responseCase.setId(responseCaseDTO.getId());
//    responseCase.setInvalid(responseCaseDTO.isInvalid());
//    responseCase.setCreatedAt(responseCaseDTO.getCreatedAt());
//    responseCase.setLastUpdatedAt(responseCaseDTO.getLastUpdatedAt());
//    responseCase.setRefusalReceived(responseCaseDTO.getRefusalReceived());
//    responseCase.setSample(responseCaseDTO.getSample());
//    AssertionsForClassTypes.assertThat(responseCase)
//        .isEqualToComparingOnlyGivenFields(
//            actualCase, "id", "caseRef", "refusalReceived", "invalid", "sample");
//    AssertionsForClassTypes.assertThat(responseCase.getCreatedAt().toEpochSecond())
//        .isEqualTo(actualCase.getCreatedAt().toEpochSecond());
//    AssertionsForClassTypes.assertThat(responseCase.getLastUpdatedAt().toEpochSecond())
//        .isEqualTo(actualCase.getLastUpdatedAt().toEpochSecond());
//  }
//
//  @Test
//  public void getMultipleCasesByUPRN() throws Exception {
//
//    Mockito.when(caseService.findCaseByFilter(UPRN_FIELD_NAME, TEST_UPRN, false))
//        .thenReturn(DataUtils.createMultipleCasesWithEvents());
//
//    mockMvc
//        .perform(
//            MockMvcRequestBuilders.get(URL_FOR_SEARCH_BY_FIELD_ENDPOINT)
//                .param("fieldName", UPRN_FIELD_NAME)
//                .param("filterValue", TEST_UPRN)
//                .accept(MediaType.APPLICATION_JSON))
//        .andExpect(MockMvcResultMatchers.status().isOk())
//        .andExpect(MockMvcResultMatchers.handler().handlerType(CaseEndpoint.class))
//
// .andExpect(MockMvcResultMatchers.handler().methodName(METHOD_NAME_FIND_CASES_BY_SAMPLE_FIELDS))
//        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Is.is(TEST1_CASE_ID)))
//        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Is.is(TEST2_CASE_ID)));
//  }
//
//  @Test
//  public void getMultipleCasesByPostcode() throws Exception {
//
//    Mockito.when(caseService.findCaseByFilter(POSTCODE_FIELD_NAME, TEST_POSTCODE, false))
//        .thenReturn(DataUtils.createMultipleCasesWithEvents());
//
//    mockMvc
//        .perform(
//            MockMvcRequestBuilders.get(URL_FOR_SEARCH_BY_FIELD_ENDPOINT)
//                .param("fieldName", POSTCODE_FIELD_NAME)
//                .param("filterValue", TEST_POSTCODE)
//                .accept(MediaType.APPLICATION_JSON))
//        .andExpect(MockMvcResultMatchers.status().isOk())
//        .andExpect(MockMvcResultMatchers.handler().handlerType(CaseEndpoint.class))
//
// .andExpect(MockMvcResultMatchers.handler().methodName(METHOD_NAME_FIND_CASES_BY_SAMPLE_FIELDS))
//        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Is.is(TEST1_CASE_ID)))
//        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Is.is(TEST2_CASE_ID)));
//  }
//
//  @Test
//  public void findCaseByFilterUPRNDoesNotExist() throws Exception {
//
//    Mockito.when(caseService.findCaseByFilter(UPRN_FIELD_NAME, TEST_UPRN, false))
//        .thenReturn(Collections.EMPTY_LIST.stream());
//
//    mockMvc
//        .perform(
//            MockMvcRequestBuilders.get(URL_FOR_SEARCH_BY_FIELD_ENDPOINT)
//                .param("fieldName", UPRN_FIELD_NAME)
//                .param("filterValue", TEST_UPRN)
//                .accept(MediaType.APPLICATION_JSON))
//        .andExpect(MockMvcResultMatchers.status().isOk())
//        .andExpect(MockMvcResultMatchers.jsonPath("$", IsCollectionWithSize.hasSize(0)));
//
//    Mockito.verify(caseService).findCaseByFilter(ArgumentMatchers.eq(UPRN_FIELD_NAME),
// ArgumentMatchers.eq(TEST_UPRN), ArgumentMatchers.eq(false));
//  }
//
//  @Test
//  public void getACaseWithEventsByCaseId() throws Exception {
//
// Mockito.when(caseService.findById(ArgumentMatchers.any())).thenReturn(DataUtils.createSingleCaseWithEvents());
//
//    mockMvc
//        .perform(
//            MockMvcRequestBuilders.get(DataUtils.createUrl("/cases/%s", TEST1_CASE_ID))
//                .param("caseEvents", "true")
//                .accept(MediaType.APPLICATION_JSON))
//        .andExpect(MockMvcResultMatchers.status().isOk())
//        .andExpect(MockMvcResultMatchers.handler().handlerType(CaseEndpoint.class))
//        .andExpect(MockMvcResultMatchers.handler().methodName(METHOD_NAME_FIND_CASE_BY_ID))
//        .andExpect(MockMvcResultMatchers.jsonPath("$.id", Is.is(TEST1_CASE_ID)))
//        .andExpect(MockMvcResultMatchers.jsonPath("$.caseEvents",
// IsCollectionWithSize.hasSize(1)));
//  }
//
//  @Test
//  public void getACaseWithoutEventsByCaseId() throws Exception {
//
// Mockito.when(caseService.findById(ArgumentMatchers.any())).thenReturn(DataUtils.createSingleCaseWithEvents());
//
//    mockMvc
//        .perform(
//            MockMvcRequestBuilders.get(DataUtils.createUrl("/cases/%s", TEST1_CASE_ID))
//                .param("caseEvents", "false")
//                .accept(MediaType.APPLICATION_JSON))
//        .andExpect(MockMvcResultMatchers.status().isOk())
//        .andExpect(MockMvcResultMatchers.handler().handlerType(CaseEndpoint.class))
//        .andExpect(MockMvcResultMatchers.handler().methodName(METHOD_NAME_FIND_CASE_BY_ID))
//        .andExpect(MockMvcResultMatchers.jsonPath("$.id", Is.is(TEST1_CASE_ID)))
//        .andExpect(MockMvcResultMatchers.jsonPath("$.caseEvents",
// IsCollectionWithSize.hasSize(0)));
//  }
//
//  @Test
//  public void getACaseWithoutEventsByDefaultByCaseId() throws Exception {
//
// Mockito.when(caseService.findById(ArgumentMatchers.any())).thenReturn(DataUtils.createSingleCaseWithEvents());
//
//    mockMvc
//        .perform(
//            MockMvcRequestBuilders.get(DataUtils.createUrl("/cases/%s",
// TEST1_CASE_ID)).accept(MediaType.APPLICATION_JSON))
//        .andExpect(MockMvcResultMatchers.status().isOk())
//        .andExpect(MockMvcResultMatchers.handler().handlerType(CaseEndpoint.class))
//        .andExpect(MockMvcResultMatchers.handler().methodName(METHOD_NAME_FIND_CASE_BY_ID))
//        .andExpect(MockMvcResultMatchers.jsonPath("$.id", Is.is(TEST1_CASE_ID)))
//        .andExpect(MockMvcResultMatchers.jsonPath("$.caseEvents",
// IsCollectionWithSize.hasSize(0)));
//  }
//
//  @Test
//  public void receiveNotFoundExceptionWhenCaseIdDoesNotExist() throws Exception {
//    Mockito.when(caseService.findById(ArgumentMatchers.any()))
//        .thenThrow(
//            new ResponseStatusException(
//                HttpStatus.NOT_FOUND, "Case not found with id: " + UUID.randomUUID()));
//
//    mockMvc
//        .perform(
//            MockMvcRequestBuilders.get(DataUtils.createUrl("/cases/%s",
// TEST1_CASE_ID)).accept(MediaType.APPLICATION_JSON))
//        .andExpect(MockMvcResultMatchers.status().isNotFound());
//  }
//
//  @Test
//  public void getACaseWithEventsByCaseReference() throws Exception {
//
// Mockito.when(caseService.findByReference(ArgumentMatchers.anyLong())).thenReturn(DataUtils.createSingleCaseWithEvents());
//
//    mockMvc
//        .perform(
//            MockMvcRequestBuilders.get(DataUtils.createUrl("/cases/ref/%s", TEST1_CASE_REF))
//                .param("caseEvents", "true")
//                .accept(MediaType.APPLICATION_JSON))
//        .andExpect(MockMvcResultMatchers.status().isOk())
//        .andExpect(MockMvcResultMatchers.handler().handlerType(CaseEndpoint.class))
//        .andExpect(MockMvcResultMatchers.handler().methodName(METHOD_NAME_FIND_CASE_BY_REFERENCE))
//        .andExpect(MockMvcResultMatchers.jsonPath("$.caseRef", Is.is(TEST1_CASE_REF)))
//        .andExpect(MockMvcResultMatchers.jsonPath("$.caseEvents",
// IsCollectionWithSize.hasSize(1)));
//  }
//
//  @Test
//  public void getACaseWithoutEventsByCaseReference() throws Exception {
//
// Mockito.when(caseService.findByReference(ArgumentMatchers.anyLong())).thenReturn(DataUtils.createSingleCaseWithEvents());
//
//    mockMvc
//        .perform(
//            MockMvcRequestBuilders.get(DataUtils.createUrl("/cases/ref/%s", TEST1_CASE_REF))
//                .param("caseEvents", "false")
//                .accept(MediaType.APPLICATION_JSON))
//        .andExpect(MockMvcResultMatchers.status().isOk())
//        .andExpect(MockMvcResultMatchers.handler().handlerType(CaseEndpoint.class))
//        .andExpect(MockMvcResultMatchers.handler().methodName(METHOD_NAME_FIND_CASE_BY_REFERENCE))
//        .andExpect(MockMvcResultMatchers.jsonPath("$.caseRef", Is.is(TEST1_CASE_REF)))
//        .andExpect(MockMvcResultMatchers.jsonPath("$.caseEvents",
// IsCollectionWithSize.hasSize(0)));
//  }
//
//  @Test
//  public void getACaseWithoutEventsByDefaultByCaseReference() throws Exception {
//
// Mockito.when(caseService.findByReference(ArgumentMatchers.anyLong())).thenReturn(DataUtils.createSingleCaseWithEvents());
//
//    mockMvc
//        .perform(
//            MockMvcRequestBuilders.get(DataUtils.createUrl("/cases/ref/%s", TEST1_CASE_REF))
//                .accept(MediaType.APPLICATION_JSON))
//        .andExpect(MockMvcResultMatchers.status().isOk())
//        .andExpect(MockMvcResultMatchers.handler().handlerType(CaseEndpoint.class))
//        .andExpect(MockMvcResultMatchers.handler().methodName(METHOD_NAME_FIND_CASE_BY_REFERENCE))
//        .andExpect(MockMvcResultMatchers.jsonPath("$.caseRef", Is.is(TEST1_CASE_REF)))
//        .andExpect(MockMvcResultMatchers.jsonPath("$.caseEvents",
// IsCollectionWithSize.hasSize(0)));
//  }
//
//  @Test
//  public void receiveNotFoundExceptionWhenCaseReferenceDoesNotExist() throws Exception {
//    Mockito.when(caseService.findByReference(ArgumentMatchers.anyLong()))
//        .thenThrow(
//            new ResponseStatusException(
//                HttpStatus.NOT_FOUND, "Case not found with reference: " + 0));
//
//    mockMvc
//        .perform(
//            MockMvcRequestBuilders.get(DataUtils.createUrl("/cases/ref/%s", TEST1_CASE_REF))
//                .accept(MediaType.APPLICATION_JSON))
//        .andExpect(MockMvcResultMatchers.status().isNotFound());
//
//
// Mockito.verify(caseService).findByReference(ArgumentMatchers.eq(Long.parseLong(TEST1_CASE_REF)));
//  }
// }
