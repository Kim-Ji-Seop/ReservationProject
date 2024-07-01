package org.boot.reservationproject.domain.customer.user.repository;

import java.util.Optional;
import org.boot.reservationproject.domain.customer.user.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
  Optional<CustomerEntity> findByEmail(String email);
}
