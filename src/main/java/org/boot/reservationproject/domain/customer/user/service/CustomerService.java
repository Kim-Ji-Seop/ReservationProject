package org.boot.reservationproject.domain.customer.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.customer.user.dto.request.SignUpRequest;
import org.boot.reservationproject.domain.customer.user.dto.response.SignUpResponse;
import org.boot.reservationproject.domain.customer.user.entity.CustomerEntity;
import org.boot.reservationproject.domain.customer.user.entity.CustomerEntity.Gender;
import org.boot.reservationproject.domain.customer.user.repository.CustomerRepository;
import org.boot.reservationproject.global.error.BaseException;
import org.boot.reservationproject.global.error.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {
  private final CustomerRepository customerRepository;
  private final PasswordEncoder passwordEncoder;
  public SignUpResponse signUp(SignUpRequest request) {
    try{
      if(request == null
          || request.email().isEmpty()
          || request.password().isEmpty()
          || request.nickname().isEmpty()){
        throw new BaseException(ErrorCode.BAD_REQUEST); // 빈 값
      }

      // 1. 비밀번호 암호화
      String encodedPassword;
      try {
        encodedPassword = passwordEncoder.encode(request.password());
      } catch (Exception e) {
        throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, "Password encoding failed", e);
      }
      log.info("SignUp Method => before pw : "+request.password()
          + " | " + "after store pw :" + encodedPassword);
      // 2. 데이터 삽입
      CustomerEntity newCustomer = CustomerEntity.builder()
          .email(request.email())
          .password(encodedPassword)
          .phoneNumber(request.phoneNumber())
          .birthday(request.birthday())
          .gender(request.gender().equals("MALE") ? Gender.MALE : Gender.FEMALE)
          .name("")
          .nickname(request.nickname())
          .build();
      customerRepository.save(newCustomer);
      // 3. Response
      return new SignUpResponse(true);
    }catch (BaseException e){
      log.error("SignUp failed: ", e);
      throw e;
    }catch (Exception e){
      log.error("Unexpected error during signUp: ", e);
      throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error", e);
    }

  }
}
