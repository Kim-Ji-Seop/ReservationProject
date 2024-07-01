package org.boot.reservationproject.domain.seller.manager;

import lombok.RequiredArgsConstructor;
import org.boot.reservationproject.domain.customer.user.dto.request.SignUpRequest;
import org.boot.reservationproject.domain.customer.user.dto.response.SignUpResponse;
import org.boot.reservationproject.domain.seller.manager.dto.request.ManagerSignUpRequest;
import org.boot.reservationproject.domain.seller.manager.dto.response.ManagerSignUpResponse;
import org.boot.reservationproject.domain.seller.manager.service.ManagerService;
import org.boot.reservationproject.global.error.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class ManagerController {
  private final ManagerService managerService;
  /*
   * 기업 대표 회원가입
   * 회사 이메일, 비밀번호, 대표 전화번호, 대표 이름, 사업자 번호, 법인명, 법인주소 입력
   */
  @PostMapping("/registration")
  public ResponseEntity<BaseResponse<ManagerSignUpResponse>> signUp(@RequestBody ManagerSignUpRequest request){
    ManagerSignUpResponse response = managerService.signUp(request);
    return ResponseEntity.ok(new BaseResponse<>(response));
  }
}
