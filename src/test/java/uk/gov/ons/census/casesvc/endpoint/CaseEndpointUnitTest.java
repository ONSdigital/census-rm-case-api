package uk.gov.ons.census.casesvc.endpoint;



public class CaseEndpointUnitTest {

  //  private static final String METHOD_NAME_FIND_CASE_BY_ID = "findByCaseId";
  //
  //  private UUID TEST1_CASE_ID;
  //
  //  @Mock private CaseService caseService;
  //
  //  @Spy private MapperFacade mapperFacade = new CaseSvcBeanMapper();
  //
  //  @InjectMocks private CaseEndpoint caseEndpoint;
  //
  //  private MockMvc mockMvc;
  //
  //  @Before
  //  public void setUp() {
  //    MockitoAnnotations.initMocks(this);
  //
  //    this.mockMvc =
  //        MockMvcBuilders.standaloneSetup(caseEndpoint)
  //            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
  //            .setMessageConverters(new MappingJackson2HttpMessageConverter(new
  // CustomObjectMapper()))
  //            .build();
  //
  //    this.TEST1_CASE_ID = UUID.randomUUID();
  //  }
  //
  //  @Test
  //  public void shouldReturnCaseWhenCaseIdExists() throws Exception {
  //
  //    Case testCase = createTestCase();
  //
  //    when(caseService.findByCaseId(anyObject())).thenReturn(testCase);
  //
  //    ResultActions actions =
  //        mockMvc.perform(getJson(String.format("/cases/%s", TEST1_CASE_ID.toString())));
  //
  //    actions.andExpect(status().isOk());
  //    actions.andExpect(handler().handlerType(CaseEndpoint.class));
  //    actions.andExpect(handler().methodName(METHOD_NAME_FIND_CASE_BY_ID));
  //    actions.andExpect(jsonPath("$.caseId", is(TEST1_CASE_ID.toString())));
  //  }
  //
  //  private Case createTestCase() {
  //    Case caze = new Case();
  //
  //    caze.setCaseId(TEST1_CASE_ID);
  //
  //    return caze;
  //  }
}
