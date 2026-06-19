package uk.gov.ons.census.caseapisvc.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.ons.census.caseapisvc.client.UacQidServiceClient;
import uk.gov.ons.census.caseapisvc.model.dto.EventDTO;
import uk.gov.ons.census.caseapisvc.model.dto.NewQidLink;
import uk.gov.ons.census.caseapisvc.model.dto.UacQidCreatedPayloadDTO;
import uk.gov.ons.census.caseapisvc.model.repository.UacQidLinkRepository;
import uk.gov.ons.census.caseapisvc.utility.PubSubHelper;
import uk.gov.ons.census.common.model.entity.Case;
import uk.gov.ons.census.common.model.entity.UacQidLink;

public class UacQidServiceTest {

  private UacQidServiceClient uacQidServiceClient;
  private UacQidLinkRepository uacQidLinkRepository;
  private PubSubHelper pubSubHelper;

  private UacQidService service;

  @BeforeEach
  void setup() {
    uacQidServiceClient = mock(UacQidServiceClient.class);
    uacQidLinkRepository = mock(UacQidLinkRepository.class);
    pubSubHelper = mock(PubSubHelper.class);

    service = new UacQidService(uacQidServiceClient, uacQidLinkRepository, pubSubHelper);

    service.questionnaireLinkedTopic = "questionnaire-linked";
    service.pubsubProject = "test-project";
  }

  // ----------------------------------------------------------------------
  // createAndLinkUacQid
  // ----------------------------------------------------------------------
  @Test
  void createAndLinkUacQid_setsCaseId() {
    UUID caseId = UUID.randomUUID();
    UacQidCreatedPayloadDTO dto = new UacQidCreatedPayloadDTO();
    when(uacQidServiceClient.generateUacQid(1)).thenReturn(dto);

    UacQidCreatedPayloadDTO result = service.createAndLinkUacQid(caseId, 1);

    assertEquals(caseId, result.getCaseId());
    verify(uacQidServiceClient).generateUacQid(1);
  }

  // ----------------------------------------------------------------------
  // findUacQidLinkByQid
  // ----------------------------------------------------------------------
  @Test
  void findUacQidLinkByQid_returnsLink() {
    UacQidLink link = new UacQidLink();
    when(uacQidLinkRepository.findByQid("QID123")).thenReturn(Optional.of(link));

    UacQidLink result = service.findUacQidLinkByQid("QID123");

    assertSame(link, result);
  }

  @Test
  void findUacQidLinkByQid_throwsWhenNotFound() {
    when(uacQidLinkRepository.findByQid("QID123")).thenReturn(Optional.empty());

    assertThrows(ResponseStatusException.class, () -> service.findUacQidLinkByQid("QID123"));
  }

  // ----------------------------------------------------------------------
  // calculateQuestionnaireType
  // ----------------------------------------------------------------------
  @Test
  void calculateQuestionnaireType_householdEngland() {
    int result = UacQidService.calculateQuestionnaireType("HH", "E123", "U", "CENSUS");
    assertEquals(1, result);
  }

  @Test
  void calculateQuestionnaireType_individualWales() {
    int result = UacQidService.calculateQuestionnaireType("HH", "W123", "U", "CENSUS", true);
    assertEquals(22, result);
  }

  @Test
  void calculateQuestionnaireType_ceEstabNI() {
    int result = UacQidService.calculateQuestionnaireType("CE", "N123", "E", "CENSUS");
    assertEquals(34, result);
  }

  @Test
  void calculateQuestionnaireType_ccsAlways71() {
    int result = UacQidService.calculateQuestionnaireType("HH", "E123", "U", "CCS");
    assertEquals(71, result);
  }

  @Test
  void calculateQuestionnaireType_invalidCountry_throws() {
    assertThrows(
        IllegalArgumentException.class,
        () -> UacQidService.calculateQuestionnaireType("HH", "X999", "U", "CENSUS"));
  }

  // ----------------------------------------------------------------------
  // buildAndSendQuestionnaireLinkedEvent
  // ----------------------------------------------------------------------
  @Test
  void buildAndSendQuestionnaireLinkedEvent_sendsMessageToPubSub() {
    // Arrange
    UacQidLink link = new UacQidLink();
    link.setQid("QID123");

    Case caze = new Case();
    UUID caseId = UUID.randomUUID();
    caze.setId(caseId);
    UUID tranxId = UUID.randomUUID();

    NewQidLink newQidLink = new NewQidLink();
    newQidLink.setChannel("WEB");
    newQidLink.setTransactionId(tranxId);

    // Capture arguments
    ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<EventDTO> eventCaptor = ArgumentCaptor.forClass(EventDTO.class);

    try {
      // Act
      service.buildAndSendQuestionnaireLinkedEvent(link, caze, newQidLink);
    } catch (HttpServerErrorException.NotImplemented ex) {
      // Assert topic
      verify(pubSubHelper).publishAndConfirm(topicCaptor.capture(), eventCaptor.capture());
      // verify(messageSender).sendMessage(topicCaptor.capture(), eventCaptor.capture());
      String topic = topicCaptor.getValue();
      assertEquals("questionnaire-linked", topic);

      // Assert event payload
      EventDTO event = eventCaptor.getValue();
      assertEquals("WEB", event.getHeader().getChannel());
      assertEquals(tranxId, event.getHeader().getMessageId());
      assertEquals("questionnaire-linked", event.getHeader().getTopic());
      assertNotNull(event.getHeader().getDateTime());

      assertEquals(caseId, event.getPayload().getUac().getCaseId());
      assertEquals("QID123", event.getPayload().getUac().getQuestionnaireId());
    }
  }
}
