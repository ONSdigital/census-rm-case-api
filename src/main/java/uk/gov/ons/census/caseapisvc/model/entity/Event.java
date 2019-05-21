package uk.gov.ons.census.caseapisvc.model.entity;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import lombok.Data;
import uk.gov.ons.census.caseapisvc.model.dto.EventType;

@Data
@Entity
public class Event {
  @Id private UUID id;

  @ManyToOne private UacQidLink uacQidLink;

  // TODO remove @Transient when field in schema
  @Column @Transient private EventType eventType;

  @Column private Date eventDate;

  @Column private String eventDescription;
}
