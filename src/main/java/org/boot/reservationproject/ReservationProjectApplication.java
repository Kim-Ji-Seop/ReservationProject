package org.boot.reservationproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ReservationProjectApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReservationProjectApplication.class, args);
  }

}
