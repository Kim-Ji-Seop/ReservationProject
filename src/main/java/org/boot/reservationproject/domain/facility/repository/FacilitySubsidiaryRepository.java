package org.boot.reservationproject.domain.facility.repository;

import java.util.List;
import java.util.Optional;
import org.boot.reservationproject.domain.facility.entity.FacilitySubsidiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilitySubsidiaryRepository extends JpaRepository<FacilitySubsidiary,Long> {
  @Query("SELECT s.subsidiaryInformation "
      + "FROM FacilitySubsidiary fs "
      + "JOIN fs.subsidiary s "
      + "WHERE fs.facility.id = :facilityIdx")
  Optional<List<String>> findSubsidiariesByFacilityIdx(@Param("facilityIdx") Long facilityIdx);

}
