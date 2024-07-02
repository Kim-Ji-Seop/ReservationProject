package org.boot.reservationproject.domain.seller.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.customer.entity.CustomerEntity;
import org.boot.reservationproject.domain.seller.dto.request.SellerSignInRequest;
import org.boot.reservationproject.domain.seller.dto.request.SellerSignUpRequest;
import org.boot.reservationproject.domain.seller.dto.response.SellerSignInResponse;
import org.boot.reservationproject.domain.seller.dto.response.SellerSignUpResponse;
import org.boot.reservationproject.domain.seller.entity.SellerEntity;
import org.boot.reservationproject.domain.seller.repository.SellerRepository;
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
@RequiredArgsConstructor
@Slf4j
public class SellerService {
  private final PasswordEncoder passwordEncoder;
  private final SellerRepository sellerRepository;
  private final CustomUserDetailService customUserDetailService;
  private final JwtTokenProvider jwtTokenProvider;
  public SellerSignUpResponse signUp(SellerSignUpRequest request) {
    try{
      if(request == null
          || request.cpEmail().isEmpty()
          || request.password().isEmpty()
          || request.epPhoneNumber().isEmpty()
          || request.epName().isEmpty()
          || request.epCode().isEmpty()
          || request.cpName().isEmpty()
          || request.cpLocation().isEmpty()){
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
      SellerEntity newSeller = SellerEntity.builder()
          .cpEmail(request.cpEmail())
          .cpPassword(encodedPassword)
          .epPhoneNumber(request.epPhoneNumber())
          .epName(request.epName())
          .epCode(request.epCode())
          .cpName(request.cpName())
          .cpLocation(request.cpLocation())
          .role(Role.SELLER)
          .build();
      sellerRepository.save(newSeller);
      // 3. Response
      return new SellerSignUpResponse(true);
    }catch (BaseException e){
      log.error("SignUp failed: ", e);
      throw e;
    }catch (Exception e){
      log.error("Unexpected error during signUp: ", e);
      throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error", e);
    }
  }

  public SellerSignInResponse signIn(SellerSignInRequest request) {
    if(request == null ||
        request.cpEmail().isEmpty() ||
        request.cpPassword().isEmpty()){
      throw new BaseException(ErrorCode.BAD_REQUEST); // 빈 값
    }
    UserDetails userDetails = customUserDetailService
        .loadUserByUsername(request.cpEmail());
    if(!checkPassword(request.cpPassword(), userDetails.getPassword())){ // 비밀번호 비교
      throw new BaseException(ErrorCode.BAD_REQUEST);
    }
    //List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(customer.getRole().toString()));
    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    TokenDto token = jwtTokenProvider.generateToken(authentication);

    SellerEntity seller = (SellerEntity) userDetails;
    log.info("유저 권한 : {}", seller.getAuthorities().toString());
    return SellerSignInResponse.builder()
        .epName(seller.getEpName())
        .cpName(seller.getCpName())
        .tokenDto(token)
        .build();
  }
  public boolean checkPassword(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }
}
