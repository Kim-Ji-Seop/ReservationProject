package org.boot.reservationproject.domain.customer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.customer.dto.request.SignInRequest;
import org.boot.reservationproject.domain.customer.dto.request.SignUpRequest;
import org.boot.reservationproject.domain.customer.dto.request.UpdateCustomerInfoRequest;
import org.boot.reservationproject.domain.customer.dto.response.SignInResponse;
import org.boot.reservationproject.domain.customer.entity.Customer;
import org.boot.reservationproject.domain.customer.repository.CustomerRepository;
import org.boot.reservationproject.global.userDetails.CustomUserDetailService;
import org.boot.reservationproject.global.Gender;
import org.boot.reservationproject.global.Role;
import org.boot.reservationproject.global.error.BaseException;
import org.boot.reservationproject.global.error.ErrorCode;
import org.boot.reservationproject.global.jwt.JwtTokenProvider;
import org.boot.reservationproject.global.jwt.TokenDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {
  private final CustomerRepository customerRepository;
  private final PasswordEncoder passwordEncoder;
  private final CustomUserDetailService customUserDetailService;
  private final JwtTokenProvider jwtTokenProvider;
  public void signUp(SignUpRequest request) {
    // 1. 비밀번호 암호화
    String encodedPassword = encodingPassword(request);
    log.info("SignUp Method => before pw : {} | after store pw : {}"
        , request.password()
        , encodedPassword);
    // 2. 데이터 삽입
    Customer newCustomer = Customer.builder()
        .email(request.email())
        .password(encodedPassword)
        .phoneNumber(request.phoneNumber())
        .birthday(request.birthday())
        .role(Role.CUSTOMER)
        .gender(Gender.valueOf(request.gender().name()))
        .nickname(request.nickname())
        .build();
    Customer customerInDB = customerRepository.save(newCustomer);
    log.info("SignUp Success? => Customer PK : {}"
        , customerInDB.getId());
  }

  public SignInResponse signIn(SignInRequest request) {
    UserDetails userDetails =
        customUserDetailService.loadUserByUsername(request.email());

    if(!checkPassword(request.password(), userDetails.getPassword())){ // 비밀번호 비교
      throw new BaseException(ErrorCode.BAD_REQUEST);
    }

    Customer customer =
        customerRepository.findByEmail(request.email())
            .orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND)
            );

    TokenDto token = jwtTokenProvider
        .generateToken(userDetails.getUsername(),userDetails.getAuthorities());

    return SignInResponse.builder()
        .userIdx(customer.getId())
        .nickname(customer.getNickname())
        .tokenDto(token)
        .build();
  }

  public boolean checkPassword(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  public String encodingPassword(SignUpRequest request){
    return passwordEncoder.encode(request.password());
  }


  public void updateCustomerInfo(String customerEmail, UpdateCustomerInfoRequest request) {
    customerRepository.updateCustomerInfo(customerEmail,request.nickname(),request.name());
  }
}
