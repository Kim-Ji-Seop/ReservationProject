package org.boot.reservationproject.domain.customer.user;

import lombok.RequiredArgsConstructor;
import org.boot.reservationproject.domain.customer.user.dto.request.SignInRequest;
import org.boot.reservationproject.domain.customer.user.dto.request.SignUpRequest;
import org.boot.reservationproject.domain.customer.user.dto.response.SignInResponse;
import org.boot.reservationproject.domain.customer.user.dto.response.SignUpResponse;
import org.boot.reservationproject.domain.customer.user.service.CustomerService;
import org.boot.reservationproject.global.error.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {
  private final CustomerService customerService;
  /*
   * 회원가입
   * 이메일, 비밀번호, 전화번호, 생년월일, 성별, 닉네임 입력
   */
  @PostMapping("/registration")
  public ResponseEntity<BaseResponse<SignUpResponse>> signUp(@RequestBody SignUpRequest request){
    SignUpResponse response = customerService.signUp(request);
    return ResponseEntity.ok(new BaseResponse<>(response));
  }

  /*
   * 로그인
   * 이메일, 비밀번호 입력
   */
  @PostMapping("/auth-email")
  public ResponseEntity<BaseResponse<SignInResponse>> signIn(@RequestBody SignInRequest request){
    SignInResponse response = customerService.signIn(request);
    return ResponseEntity.ok(new BaseResponse<>(response));
  }
}
