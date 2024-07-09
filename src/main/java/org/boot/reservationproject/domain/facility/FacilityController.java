package org.boot.reservationproject.domain.facility;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.boot.reservationproject.domain.facility.dto.response.FacilitiesInformationPreviewResponse;
import org.boot.reservationproject.domain.facility.dto.request.RegisterFacilityRequest;
import org.boot.reservationproject.domain.facility.dto.response.FacilityInformationDetailResponse;
import org.boot.reservationproject.domain.facility.dto.response.RegisterFacilityResponse;
import org.boot.reservationproject.domain.facility.service.FacilityService;
import org.boot.reservationproject.global.error.BaseResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {
  private final FacilityService facilityService;
  @PostMapping(value = "/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<RegisterFacilityResponse>> registerFacility(
      @Valid @RequestPart("facilityRequest") RegisterFacilityRequest request, // json data
      @RequestPart("facilityPhotos") List<MultipartFile> facilityPhotos) throws IOException {

    // 1. SecurityContextHolder에서 인증된 사용자 정보 추출
    String sellerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

    // 2. Service에 보내는 Param으로 추가
    RegisterFacilityResponse response = facilityService.registerFacility(request, facilityPhotos, sellerEmail);
    return ResponseEntity.ok(new BaseResponse<>(response));
  }

  @PostMapping(value = "/registration/room-photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public void registerRoomPhotos(
      @RequestParam("facilityIdx") Long facilityIdx,
      @RequestParam("roomIdx") Long roomIdx,
      @RequestPart("roomPhotos") List<MultipartFile> roomPhotos) throws IOException {

    facilityService.registerRoomPhotos(facilityIdx, roomIdx, roomPhotos);
  }

  // 시설들 카테고리별 조회
  @GetMapping( "/previews")
  public ResponseEntity<BaseResponse<List<FacilitiesInformationPreviewResponse>>>
              getFacilitiesPreview(@RequestParam("category") String category){
    List<FacilitiesInformationPreviewResponse> responses = facilityService.getFacilitiesPreview(category);
    return ResponseEntity.ok(new BaseResponse<>(responses));
  }

  // 시설 상세보기 조회
  @GetMapping( "/details/{facilityIdx}")
  public ResponseEntity<BaseResponse<FacilityInformationDetailResponse>>
  getFacilityDetail(@PathVariable Long facilityIdx){
    FacilityInformationDetailResponse responses = facilityService.getFacilityDetail(facilityIdx);
    return ResponseEntity.ok(new BaseResponse<>(responses));
  }

  // 시설에 포함된 모든 사진들 조회
  // 시설에 포함된 모든 서비스 및 부대시설들 조회
}
