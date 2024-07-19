package org.boot.reservationproject.domain.customer.repository;

import java.util.Optional;
import org.boot.reservationproject.domain.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
  Optional<Customer> findByEmail(String email);

  @Modifying
  @Transactional
  @Query("UPDATE Customer c "
      + "SET c.nickname = :nickname, "
      + "c.name = :name "
      + "WHERE c.email = :customerEmail")
  void updateCustomerInfo(String customerEmail, String nickname, String name);

}
