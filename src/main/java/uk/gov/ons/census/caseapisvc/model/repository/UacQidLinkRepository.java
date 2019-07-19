package uk.gov.ons.census.caseapisvc.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.census.caseapisvc.model.entity.UacQidLink;

import java.util.Optional;
import java.util.UUID;

public interface UacQidLinkRepository extends JpaRepository<UacQidLink, UUID> {
    Optional<UacQidLink> findByQid(String qid);
}
