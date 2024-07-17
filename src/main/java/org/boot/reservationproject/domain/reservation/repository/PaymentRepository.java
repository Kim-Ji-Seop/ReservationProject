package org.boot.reservationproject.domain.reservation.repository;

import org.boot.reservationproject.domain.reservation.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity,Long> {

}
