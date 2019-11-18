package uk.gov.ons.census.caseapisvc.model.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.ons.census.caseapisvc.model.entity.Event;

public interface EventRepository extends JpaRepository<Event, UUID> {}
