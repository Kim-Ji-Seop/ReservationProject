package org.boot.reservationproject.domain.facility;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.boot.reservationproject.domain.facility.dto.request.RegisterFacilityRequest;
import org.boot.reservationproject.domain.facility.service.FacilityService;
import org.boot.reservationproject.global.jwt.JwtTokenProvider;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {
  private final JwtTokenProvider jwtTokenProvider;
  private final FacilityService facilityService;
  @PostMapping(value = "/registeration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public void registerFacility(
      @Valid @RequestPart("facilityRequest") RegisterFacilityRequest request, // json data
      @RequestPart("facilityPhotos") List<MultipartFile> facilityPhotos) throws IOException {

    // 1. SecurityContextHolder에서 인증된 사용자 정보 추출
    String sellerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

    // 2. Service에 보내는 Param으로 추가
    facilityService.registerFacility(request, facilityPhotos, sellerEmail);
  }
}
