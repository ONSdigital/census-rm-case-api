package uk.gov.ons.census.caseapisvc.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class CustomOffsetDateTimeSerializer extends JsonSerializer<OffsetDateTime> {

  @Override
  public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    gen.writeString(value.atZoneSameInstant(ZoneOffset.UTC).toString());
  }
}
