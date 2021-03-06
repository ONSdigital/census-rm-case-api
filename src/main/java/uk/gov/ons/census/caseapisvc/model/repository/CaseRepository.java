package uk.gov.ons.census.caseapisvc.model.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.gov.ons.census.caseapisvc.model.entity.Case;

public interface CaseRepository extends JpaRepository<Case, UUID> {
  Optional<List<Case>> findByUprn(String uprn);

  Optional<List<Case>> findByUprnAndAddressInvalidFalse(String uprn);

  Optional<Case> findByCaseId(UUID caseId);

  Optional<Case> findByCaseRef(long reference);

  @Query(
      "SELECT c FROM Case c WHERE UPPER(REPLACE(postcode, ' ', '')) = UPPER(REPLACE(:postcode, ' ', '')) "
          + "ORDER BY organisationName, addressLine1, caseType, addressLevel")
  List<Case> findByPostcode(@Param("postcode") String postcode);

  @Query(
      "SELECT c FROM Case c WHERE survey='CCS' AND UPPER(REPLACE(postcode, ' ', '')) = UPPER(REPLACE(:postcode, ' ', ''))")
  List<Case> findCCSCasesByPostcodeIgnoringCaseAndSpaces(@Param("postcode") String postcode);

  boolean existsCaseByCaseId(UUID caseId);
}
