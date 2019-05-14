package uk.gov.ons.census.caseapisvc.model.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.ons.census.caseapisvc.model.entity.Case;

public interface CaseRepository extends JpaRepository<Case, UUID> {
  Optional<List<Case>> findByUprn(String uprn);

  Optional<Case> findByCaseId(UUID caseId);

  Optional<Case> findByCaseRef(Long reference);
}
