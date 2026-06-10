package uk.gov.ons.census.caseapisvc.utility;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import uk.gov.ons.census.caseapisvc.model.dto.EventHeaderDTO;

public class EventHelperTest {

  @Test
  public void testCreateEventDTOWithEventType() {
    EventHeaderDTO eventHeader = EventHelper.createEventDTO("Test topic");

    assertThat(eventHeader.getChannel()).isEqualTo("RM");
    assertThat(eventHeader.getSource()).isEqualTo("CASE_API");
    assertThat(eventHeader.getDateTime()).isInstanceOf(OffsetDateTime.class);
    assertThat(eventHeader.getMessageId()).isInstanceOf(UUID.class);
    assertThat(eventHeader.getCorrelationId()).isInstanceOf(UUID.class);
    assertThat(eventHeader.getTopic()).isEqualTo("Test topic");
  }

  @Test
  public void testCreateEventDTOWithEventTypeChannelAndSource() {
    EventHeaderDTO eventHeader = EventHelper.createEventDTO("Test topic", "CHANNEL", "SOURCE");

    assertThat(eventHeader.getChannel()).isEqualTo("CHANNEL");
    assertThat(eventHeader.getSource()).isEqualTo("SOURCE");
    assertThat(eventHeader.getDateTime()).isInstanceOf(OffsetDateTime.class);
    assertThat(eventHeader.getMessageId()).isInstanceOf(UUID.class);
    assertThat(eventHeader.getCorrelationId()).isInstanceOf(UUID.class);
    assertThat(eventHeader.getTopic()).isEqualTo("Test topic");
  }
}
