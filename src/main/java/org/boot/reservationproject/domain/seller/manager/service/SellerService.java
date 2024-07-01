package org.boot.reservationproject.domain.seller.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.seller.manager.dto.request.SellerSignUpRequest;
import org.boot.reservationproject.domain.seller.manager.dto.response.SellerSignUpResponse;
import org.boot.reservationproject.domain.seller.manager.entity.SellerEntity;
import org.boot.reservationproject.domain.seller.manager.repository.SellerRepository;
import org.boot.reservationproject.global.Role;
import org.boot.reservationproject.global.error.BaseException;
import org.boot.reservationproject.global.error.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerService {
  private final PasswordEncoder passwordEncoder;
  private final SellerRepository sellerRepository;
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
      log.info("SignUp Method => before pw : "+request.password()
          + " | " + "after store pw :" + encodedPassword);
      // 2. 데이터 삽입
      SellerEntity newSeller = SellerEntity.builder()
          .cpEmail(request.cpEmail())
          .cpPassword(request.password())
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
}
