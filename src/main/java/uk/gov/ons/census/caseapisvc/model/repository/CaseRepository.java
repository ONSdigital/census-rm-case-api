package uk.gov.ons.census.caseapisvc.model.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.gov.ons.census.common.model.entity.Case;

public interface CaseRepository extends JpaRepository<Case, UUID> {

  @Override
  Optional<Case> findById(UUID id);

  Optional<Case> findByCaseRef(long reference);

  Optional<List<Case>> findByUprn(String uprn);

  Optional<List<Case>> findByUprnAndInvalidFalse(String uprn);

  @Query(
      """
    SELECT c
    FROM Case c
    WHERE UPPER(REPLACE(c.postcode, ' ', '')) = UPPER(REPLACE(:postcode, ' ', ''))
    ORDER BY c.organisationName, c.addressLine1, c.caseType, c.addressLevel
""")
  List<Case> findByPostcode(@Param("postcode") String postcode);
}
