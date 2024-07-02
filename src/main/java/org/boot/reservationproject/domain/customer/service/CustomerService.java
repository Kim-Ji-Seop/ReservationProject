package org.boot.reservationproject.domain.customer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.customer.dto.request.SignInRequest;
import org.boot.reservationproject.domain.customer.dto.request.SignUpRequest;
import org.boot.reservationproject.domain.customer.dto.response.SignInResponse;
import org.boot.reservationproject.domain.customer.dto.response.SignUpResponse;
import org.boot.reservationproject.domain.customer.entity.CustomerEntity;
import org.boot.reservationproject.domain.customer.entity.CustomerEntity.Gender;
import org.boot.reservationproject.domain.customer.repository.CustomerRepository;
import org.boot.reservationproject.global.CustomUserDetailService;
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

  public SignInResponse signIn(SignInRequest request) {
    if(request == null ||
        request.email().isEmpty() ||
        request.password().isEmpty()){
      throw new BaseException(ErrorCode.BAD_REQUEST); // 빈 값
    }
    UserDetails userDetails = customUserDetailService
                                              .loadUserByUsername(request.email());
    if(!checkPassword(request.password(), userDetails.getPassword())){ // 비밀번호 비교
      throw new BaseException(ErrorCode.BAD_REQUEST);
    }
    //List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(customer.getRole().toString()));
    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    TokenDto token = jwtTokenProvider.generateToken(authentication);

    CustomerEntity customer = (CustomerEntity) userDetails;
    log.info("유저 권한 : {}", customer.getAuthorities().toString());
    return SignInResponse.builder()
        .nickname(customer.getNickname())
        .tokenDto(token)
        .build();
  }

  public boolean checkPassword(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }
}
