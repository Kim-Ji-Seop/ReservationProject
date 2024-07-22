package org.boot.reservationproject.domain.review.repository;

import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.domain.review.entity.Review;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {
  Page<Review> findByFacilityId(Long facilityId, Pageable pageable);
}
