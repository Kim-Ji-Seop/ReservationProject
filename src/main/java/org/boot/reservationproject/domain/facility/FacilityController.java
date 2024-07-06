package org.boot.reservationproject.domain.facility;

import jakarta.validation.Valid;
import java.util.List;
import org.boot.reservationproject.domain.facility.dto.request.RegisterFacilityRequest;
import org.boot.reservationproject.domain.facility.dto.response.RegisterFacilityResponse;
import org.boot.reservationproject.global.error.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/facilites")
public class FacilityController {
  /*
   * 시설 등록 - 판매자
    * 시설 카테고리 (모텔,호텔,리조트,펜션,풀빌라,캠핑,글램핑)
    * 시설 이름
    * 연락처
      * 회사 연락처
    * 시설 내 객실 상세정보
      * 객실 이름
      * 객실 사진 (최대 10장)
      * 기준인원 / 최대인원
      * 체크인 / 체크아웃 시간
      * 가격
    * 사진 등록 (최대 갯수 : 20장 제한)
    * 서비스 및 부대시설 등록
    * 취소 및 환불 규정
    * 위치 등록
  */
  public ResponseEntity<BaseResponse<RegisterFacilityResponse>> registerFacility(
      @Valid @RequestPart RegisterFacilityRequest request, // json data
      @RequestPart List<MultipartFile> facilityPhotos, // 시설 사진
      @RequestPart List<MultipartFile> roomPhotos, // 객실 사진
      @RequestHeader("Authorization") String token){

    return null;

  }
}
