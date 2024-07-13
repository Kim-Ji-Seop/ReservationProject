package org.boot.reservationproject.domain.facility.repository;

import java.util.Optional;
import org.boot.reservationproject.domain.facility.entity.Subsidiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubsidiaryRepository extends JpaRepository<Subsidiary,Long> {
  @Query("SELECT s FROM Subsidiary s WHERE s.subsidiaryInformation = :subsidiaryInformation")
  Optional<Subsidiary> findBySubsidiaryInformation(@Param("subsidiaryInformation") String subsidiaryInformation);
}
