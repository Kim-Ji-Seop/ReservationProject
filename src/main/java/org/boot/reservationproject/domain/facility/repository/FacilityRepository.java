package org.boot.reservationproject.domain.facility.repository;

import java.util.List;
import org.boot.reservationproject.domain.facility.entity.Facility;
import org.boot.reservationproject.global.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility,Long> {
  List<Facility> findByCategory(Category category);
}
