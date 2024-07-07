package org.boot.reservationproject.domain.facility.repository;

import org.boot.reservationproject.domain.facility.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service,Long> {

}
