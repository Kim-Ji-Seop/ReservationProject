package org.boot.reservationproject.domain.facility;

import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.domain.facility.dto.request.RegisterRoomRequest;
import org.boot.reservationproject.domain.facility.dto.request.UpdateFacilityRequest;
import org.boot.reservationproject.domain.facility.dto.request.RegisterFacilityRequest;
import org.boot.reservationproject.domain.facility.dto.response.FacilitiesPageResponse;
import org.boot.reservationproject.domain.facility.dto.response.FacilityInformationDetailResponse;
import org.boot.reservationproject.domain.facility.dto.response.RegisterFacilityResponse;
import org.boot.reservationproject.domain.facility.service.FacilityService;
import org.boot.reservationproject.global.Category;
import org.boot.reservationproject.global.convertor.CategoryConverter;
import org.boot.reservationproject.global.error.BaseResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
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
@Slf4j
public class FacilityController {
  private final FacilityService facilityService;
  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(Category.class, new CategoryConverter());
  }
  @PostMapping(value = "/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<RegisterFacilityResponse>> registerFacility(
      @Valid @RequestPart("facilityRequest") RegisterFacilityRequest request, // json data
      @RequestPart("facilityPhotos") List<MultipartFile> facilityPhotos) throws IOException {

    // 1. SecurityContextHolder에서 인증된 사용자 정보 추출
    String sellerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

    // 2. Service에 보내는 Param으로 추가
    RegisterFacilityResponse response =
        facilityService.registerFacility(request, facilityPhotos, sellerEmail);
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
  public ResponseEntity<BaseResponse<FacilitiesPageResponse>>
              getFacilitiesPreview(
                  @RequestParam("category") Category category,
                  @RequestParam("checkIn") LocalDate checkInDate,
                  @RequestParam("checkOut") LocalDate checkOutDate,
                  @RequestParam("personal") int personal,
                  Pageable pageable){
    FacilitiesPageResponse responses =
        facilityService.getFacilitiesPreview(category, checkInDate, checkOutDate, personal, pageable);
    return ResponseEntity.ok(new BaseResponse<>(responses));
  }

  // 시설 상세보기 조회
  @GetMapping( "/details/{facilityIdx}")
  public ResponseEntity<BaseResponse<FacilityInformationDetailResponse>>
              getFacilityDetail(
                  @PathVariable Long facilityIdx,
                  @RequestParam("checkIn") LocalDate checkInDate,
                  @RequestParam("checkOut") LocalDate checkOutDate,
                  @RequestParam("personal") int personal){
    FacilityInformationDetailResponse responses =
        facilityService.getFacilityDetail(facilityIdx, checkInDate, checkOutDate, personal);
    return ResponseEntity.ok(new BaseResponse<>(responses));
  }

  // 시설정보 수정 - 기본 텍스트, 시설 사진, 객실
  @PatchMapping(value = "/{facilityIdx}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public void updateFacility(
      @PathVariable Long facilityIdx,
      @Valid @RequestPart("facilityRequest") UpdateFacilityRequest request,
      @RequestPart("facilityPhotos") List<MultipartFile> facilityPhotos
  ) throws IOException {

    facilityService.updateFacility(facilityIdx, request, facilityPhotos);
  }

  // 객실 사진 수정
  @PatchMapping(value = "/room-photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public void updateRoomPhotos(
      @RequestParam("facilityIdx") Long facilityIdx,
      @RequestParam("roomIdx") Long roomIdx,
      @RequestPart("roomPhotos") List<MultipartFile> roomPhotos) throws IOException {

    facilityService.updateRoomPhotos(facilityIdx, roomIdx, roomPhotos);
  }

  // 객실 추가(하나씩 추가) - ES 동기화
  @PostMapping(value = "/registration/additional-rooms", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public void registerRooms(
      @RequestParam("facilityIdx") Long facilityIdx,
      @Valid @RequestPart("roomRequest") RegisterRoomRequest request,
      @RequestPart("roomPhotos") List<MultipartFile> roomPhotos) throws IOException {

    facilityService.registerRooms(facilityIdx, request, roomPhotos);
  }

  // 객실 삭제 - ES 동기화 (소프트 딜리트)
  @PatchMapping("/rooms/inactive")
  public void deleteRooms(
      @RequestParam("facilityIdx") Long facilityIdx,
      @RequestParam("roomIdx") Long roomIdx) throws IOException {

    facilityService.deleteRooms(facilityIdx,roomIdx);
  }

  // 시설에 포함된 모든 서비스 및 부대시설들 조회

  // 시설, 객실들 사진 전체 조회 > 쿼리스트링
}
