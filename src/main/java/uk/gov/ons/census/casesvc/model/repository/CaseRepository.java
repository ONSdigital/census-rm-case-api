package uk.gov.ons.census.casesvc.model.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import uk.gov.ons.census.casesvc.model.entity.Case;

@RestResource(exported = false)
public interface CaseRepository extends JpaRepository<Case, UUID> {
  Optional<List<Case>> findByuprn(String uprn);

  Optional<Case> findByCaseId(UUID caseId);

  Optional<Case> findByCaseRef(Long reference);
}
