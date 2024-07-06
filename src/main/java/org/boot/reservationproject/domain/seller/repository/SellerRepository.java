package org.boot.reservationproject.domain.seller.repository;

import java.util.Optional;
import org.boot.reservationproject.domain.seller.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller,Long> {
  Optional<Seller> findByCpEmail(String email);
}
