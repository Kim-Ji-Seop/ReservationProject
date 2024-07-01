package org.boot.reservationproject.domain.seller.manager.repository;

import org.boot.reservationproject.domain.seller.manager.entity.SellerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends JpaRepository<SellerEntity,Long> {

}
