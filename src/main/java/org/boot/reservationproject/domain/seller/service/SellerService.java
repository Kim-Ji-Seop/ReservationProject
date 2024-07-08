package org.boot.reservationproject.domain.seller.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.seller.dto.request.SellerSignInRequest;
import org.boot.reservationproject.domain.seller.dto.request.SellerSignUpRequest;
import org.boot.reservationproject.domain.seller.dto.response.SellerSignInResponse;
import org.boot.reservationproject.domain.seller.entity.Seller;
import org.boot.reservationproject.domain.seller.repository.SellerRepository;
import org.boot.reservationproject.global.userDetails.CustomUserDetailService;
import org.boot.reservationproject.global.Role;
import org.boot.reservationproject.global.error.BaseException;
import org.boot.reservationproject.global.error.ErrorCode;
import org.boot.reservationproject.global.jwt.JwtTokenProvider;
import org.boot.reservationproject.global.jwt.TokenDto;
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
  public void signUp(SellerSignUpRequest request) {
    // 1. 비밀번호 암호화
    String encodedPassword = encodingPassword(request);

    log.info("SignUp Method => before pw : {} | after store pw : {}"
        , request.password()
        , encodedPassword);

    // 2. 데이터 삽입
    Seller newSeller = Seller.builder()
        .cpEmail(request.cpEmail())
        .cpPassword(encodedPassword)
        .phoneNumber(request.phoneNumber())
        .epName(request.epName())
        .epCode(request.epCode())
        .cpName(request.cpName())
        .cpLocation(request.cpLocation())
        .role(Role.SELLER)
        .build();
    Seller sellerInDB = sellerRepository.save(newSeller);
    log.info("SignUp Success? => Seller PK : {}"
        , sellerInDB.getId());
  }

  public SellerSignInResponse signIn(SellerSignInRequest request) {
    UserDetails userDetails =
        customUserDetailService.loadUserByUsername(request.cpEmail());
    if(!checkPassword(request.cpPassword(), userDetails.getPassword())){ // 비밀번호 비교
      throw new BaseException(ErrorCode.BAD_REQUEST);
    }

    Seller seller =
        sellerRepository.findByCpEmail(request.cpEmail())
            .orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND)
            );

    TokenDto token = jwtTokenProvider
        .generateToken(
            userDetails.getUsername(),
            userDetails.getAuthorities());

    return SellerSignInResponse.builder()
        .epName(seller.getEpName())
        .cpName(seller.getCpName())
        .tokenDto(token)
        .build();
  }
  public boolean checkPassword(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  public String encodingPassword(SellerSignUpRequest request){
    return passwordEncoder.encode(request.password());
  }
}
