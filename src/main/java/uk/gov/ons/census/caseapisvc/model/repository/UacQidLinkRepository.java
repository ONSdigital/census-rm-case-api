package uk.gov.ons.census.caseapisvc.model.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.ons.census.caseapisvc.model.entity.UacQidLink;

public interface UacQidLinkRepository extends JpaRepository<UacQidLink, UUID> {
  Optional<UacQidLink> findByQid(String qid);

  Optional<UacQidLink> findOneByCcsCaseIsTrueAndCazeCaseIdAndCazeSurvey(UUID caseIdUUID, String survey);
}
