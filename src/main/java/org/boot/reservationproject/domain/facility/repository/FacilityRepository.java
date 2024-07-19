package org.boot.reservationproject.domain.facility.repository;

import java.math.BigDecimal;
import java.util.Optional;
import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.global.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FacilityRepository extends JpaRepository<Facility,Long> {
  Page<Facility> findAll(Pageable pageable);

  Page<Facility> findByCategory(Category category, Pageable pageable);

  @Query("SELECT f "
      + "FROM Facility f "
      + "LEFT JOIN FETCH f.rooms "
      + "WHERE f.id = :facilityIdx")
  Optional<Facility> findFacilityWithRooms(@Param("facilityIdx") Long facilityIdx);

  @Modifying(clearAutomatically=true) // JPA 1차캐시 <-> DB 동기화 문제
  @Transactional
  @Query("UPDATE Facility f "
      + "SET f.facilityName = :facilityName, "
          + "f.category = :category, "
          + "f.region = :region, "
          + "f.location = :location, "
          + "f.regCancelRefund = :regCancelRefund, "
          + "f.previewFacilityPhotoUrl = :previewFacilityPhotoUrl, "
          + "f.previewFacilityPhotoName = :previewFacilityPhotoName "
      + "WHERE f.id = :facilityId")
  void updateFacility(@Param("facilityId") Long facilityId,
      @Param("facilityName") String facilityName,
      @Param("category") Category category,
      @Param("region") String region,
      @Param("location") String location,
      @Param("regCancelRefund") String regCancelRefund,
      @Param("previewFacilityPhotoUrl") String previewFacilityPhotoUrl,
      @Param("previewFacilityPhotoName") String previewFacilityPhotoName);

  @Modifying
  @Transactional
  @Query("UPDATE Facility f SET f.averageRating = :updatedAverageRating, " +
      "f.numberOfReviews = :numberOfReviews " +
      "WHERE f.id = :facilityId")
  void updateRating(@Param("facilityId") Long facilityId,
      @Param("numberOfReviews") int numberOfReviews,
      @Param("updatedAverageRating") BigDecimal updatedAverageRating);
}
