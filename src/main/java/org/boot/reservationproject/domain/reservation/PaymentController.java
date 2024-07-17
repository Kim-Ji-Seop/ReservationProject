package org.boot.reservationproject.domain.reservation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentController {
  @GetMapping("/payment") // 테스트 결제 페이지
  public String paymentPage(Model model) {
    return "payment";
  }
}
