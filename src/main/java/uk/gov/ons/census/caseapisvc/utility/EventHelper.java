package uk.gov.ons.census.caseapisvc.utility;

import static uk.gov.ons.census.caseapisvc.utility.Constants.EVENT_SCHEMA_VERSION;

import java.time.OffsetDateTime;
import java.util.UUID;
import uk.gov.ons.census.caseapisvc.model.dto.EventHeaderDTO;

public class EventHelper {

  private static final String EVENT_SOURCE = "CASE_API";
  private static final String EVENT_CHANNEL = "RM";

  private EventHelper() {
    throw new IllegalStateException("Utility class EventHelper should not be instantiated");
  }

  public static EventHeaderDTO createEventDTO(
      String topic, String eventChannel, String eventSource) {
    EventHeaderDTO eventHeader = new EventHeaderDTO();

    eventHeader.setVersion(EVENT_SCHEMA_VERSION);
    eventHeader.setChannel(eventChannel);
    eventHeader.setSource(eventSource);
    eventHeader.setDateTime(OffsetDateTime.now());
    eventHeader.setMessageId(UUID.randomUUID());
    eventHeader.setCorrelationId(UUID.randomUUID());
    eventHeader.setTopic(topic);

    return eventHeader;
  }

  public static EventHeaderDTO createEventDTO(String topic) {
    return createEventDTO(topic, EVENT_CHANNEL, EVENT_SOURCE);
  }
}
