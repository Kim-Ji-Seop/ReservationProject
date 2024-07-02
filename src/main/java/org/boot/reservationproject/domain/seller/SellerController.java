package org.boot.reservationproject.domain.seller;

import lombok.RequiredArgsConstructor;
import org.boot.reservationproject.domain.seller.dto.request.SellerSignUpRequest;
import org.boot.reservationproject.domain.seller.dto.response.SellerSignUpResponse;
import org.boot.reservationproject.domain.seller.service.SellerService;
import org.boot.reservationproject.global.error.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerController {
  private final SellerService sellerService;
  /*
   * 기업 대표 회원가입
   * 회사 이메일, 비밀번호, 대표 전화번호, 대표 이름, 사업자 번호, 법인명, 법인주소 입력
   */
  @PostMapping("/registration")
  public ResponseEntity<BaseResponse<SellerSignUpResponse>> signUp(
                                                    @RequestBody SellerSignUpRequest request){
    SellerSignUpResponse response = sellerService.signUp(request);
    return ResponseEntity.ok(new BaseResponse<>(response));
  }
}
