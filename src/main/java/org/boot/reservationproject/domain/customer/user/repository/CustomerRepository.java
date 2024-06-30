package org.boot.reservationproject.domain.customer.user.repository;

import org.boot.reservationproject.domain.customer.user.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

}
