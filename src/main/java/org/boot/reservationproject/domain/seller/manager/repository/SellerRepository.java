package org.boot.reservationproject.domain.seller.manager.repository;

import java.util.Optional;
import org.boot.reservationproject.domain.seller.manager.entity.SellerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<SellerEntity,Long> {
  Optional<SellerEntity> findByCpEmail(String email);
}
