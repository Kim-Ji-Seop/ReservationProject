package org.boot.reservationproject.domain.customer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.customer.dto.request.SignInRequest;
import org.boot.reservationproject.domain.customer.dto.request.SignUpRequest;
import org.boot.reservationproject.domain.customer.dto.response.SignInResponse;
import org.boot.reservationproject.domain.customer.entity.CustomerEntity;
import org.boot.reservationproject.domain.customer.repository.CustomerRepository;
import org.boot.reservationproject.global.CustomUserDetailService;
import org.boot.reservationproject.global.Gender;
import org.boot.reservationproject.global.Role;
import org.boot.reservationproject.global.error.BaseException;
import org.boot.reservationproject.global.error.ErrorCode;
import org.boot.reservationproject.global.jwt.JwtTokenProvider;
import org.boot.reservationproject.global.jwt.TokenDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    CustomerEntity newCustomer = CustomerEntity.builder()
        .email(request.email())
        .password(encodedPassword)
        .phoneNumber(request.phoneNumber())
        .birthday(request.birthday())
        .role(Role.CUSTOMER)
        .gender(Gender.valueOf(request.gender().name()))
        .nickname(request.nickname())
        .build();
    CustomerEntity customerInDB = customerRepository.save(newCustomer);
    log.info("SignUp Success? => Customer PK : {}"
        , customerInDB.getId());
  }

  public SignInResponse signIn(SignInRequest request) {
    UserDetails userDetails =
        customUserDetailService.loadUserByUsername(request.email());

    if(!checkPassword(request.password(), userDetails.getPassword())){ // 비밀번호 비교
      throw new BaseException(ErrorCode.BAD_REQUEST);
    }
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
    TokenDto token = jwtTokenProvider.generateToken(authentication);

    CustomerEntity customer =
        customerRepository.findByEmail(request.email())
            .orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND)
            );
    return SignInResponse.builder()
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
}
