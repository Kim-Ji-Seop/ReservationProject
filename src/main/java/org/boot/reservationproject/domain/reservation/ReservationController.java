package org.boot.reservationproject.domain.reservation;

import lombok.RequiredArgsConstructor;
import org.boot.reservationproject.domain.reservation.service.ReservationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
  private final ReservationService reservationService;
}
