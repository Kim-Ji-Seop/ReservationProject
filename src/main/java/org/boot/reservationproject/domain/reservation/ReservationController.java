package org.boot.reservationproject.domain.reservation;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.reservation.dto.CreateReservationRequest;
import org.boot.reservationproject.domain.reservation.dto.PaymentRequest;
import org.boot.reservationproject.domain.reservation.service.ReservationService;
import org.boot.reservationproject.global.error.BaseResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
@Slf4j
@RequiredArgsConstructor
public class ReservationController {
  private final ReservationService reservationService;


  /* [POST]
   * 예약 선점
   *
   */
  @PostMapping
  public void reserve(@RequestBody CreateReservationRequest request){
    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    reservationService.reserveFacility(request, userEmail);
  }


  // 테스트 결제 완료
  @PostMapping("/payments/complete")
  public void completePayment(@RequestBody PaymentRequest request)
      throws IamportResponseException, IOException {
    log.info("mid : {}",request.merchantUid());
    reservationService.completePayment(request);
  }
}
