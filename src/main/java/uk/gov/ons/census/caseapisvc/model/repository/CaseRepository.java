package uk.gov.ons.census.caseapisvc.model.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.gov.ons.census.common.model.entity.Case;

public interface CaseRepository extends JpaRepository<Case, UUID> {

  @Override
  Optional<Case> findById(UUID id);

  Optional<Case> findByCaseRef(long reference);

  @Query(
      value = "SELECT * FROM casev3.cases WHERE sample ->> :fieldName = :filter",
      nativeQuery = true)
  Stream<Case> findCasesByField(
      @Param("fieldName") String fieldName, @Param("filter") String filter);

  @Query(
      value =
          "SELECT * FROM casev3.cases WHERE UPPER(REPLACE(sample ->> :fieldName, ' ', '')) = :filter",
      nativeQuery = true)
  Stream<Case> findCaseByFilterIgnoreCaseAndSpaces(
      @Param("fieldName") String fieldName, @Param("filter") String filter);

  Optional<List<Case>> findByUprn(String uprn);

  Optional<List<Case>> findByUprnAndAddressInvalidFalse(String uprn);

  @Query(
      value =
          "SELECT c FROM casev3.cases c WHERE UPPER(REPLACE(postcode, ' ', '')) = UPPER(REPLACE(:postcode, ' ', '')) "
              + "ORDER BY organisationName, addressLine1, caseType, addressLevel",
      nativeQuery = true)
  List<Case> findByPostcode(@Param("postcode") String postcode);
}
