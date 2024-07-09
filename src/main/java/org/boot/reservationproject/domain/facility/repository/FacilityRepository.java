package org.boot.reservationproject.domain.facility.repository;

import java.util.List;
import java.util.Optional;
import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.global.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FacilityRepository extends JpaRepository<Facility,Long> {
  List<Facility> findByCategory(Category category);

  @Query("SELECT f "
      + "FROM Facility f "
      + "LEFT JOIN FETCH f.rooms "
      + "WHERE f.id = :facilityIdx")
  Optional<Facility> findFacilityWithRooms(@Param("facilityIdx") Long facilityIdx);
}
