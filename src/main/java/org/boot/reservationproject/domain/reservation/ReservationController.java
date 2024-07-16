package org.boot.reservationproject.domain.reservation;

import com.siot.IamportRestClient.IamportClient;
import jakarta.annotation.PostConstruct;
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
  public void completePayment(@RequestBody PaymentRequest request) {
    log.info("mid : {}",request.impUid());
    log.info("mid : {}",request.merchantUid());
    log.info("mid : {}",request.customerName());
    log.info("mid : {}",request.customerPhoneNumber());
    reservationService.completePayment(request);
  }
  /* [POST]
   * 결제 준비
   *
   */

  /* [POST]
   * 시설 예약 - 결제까지 다 되었다는 전제 하에 예약 완료 API
   * Header : Customer's AccessToken
   * Body : 시설Idx, 객실Idx, 구매자Idx(Customer), 체크인날짜, 체크아웃날짜, 결제금액
   */

}
