package org.boot.reservationproject.domain.facility.repository;

import org.boot.reservationproject.domain.facility.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo,Long> {

}
