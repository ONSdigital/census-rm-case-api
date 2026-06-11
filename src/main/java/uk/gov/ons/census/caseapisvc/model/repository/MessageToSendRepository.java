package uk.gov.ons.census.caseapisvc.model.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.ons.census.common.model.entity.MessageToSend;

public interface MessageToSendRepository extends JpaRepository<MessageToSend, UUID> {}
