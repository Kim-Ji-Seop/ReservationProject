package org.boot.reservationproject.domain.facility.dto.request;

import java.util.List;
import org.boot.reservationproject.global.Category;

public record UpdateFacilityRequest(
    Category category, // 시설 카테고리
    String name, // 시설 이름
    String region, // 지역
    String location, // 상세주소
    String regCancelRefund, // 취소 및 환불 규정
    List<UpdateRoomDetail> rooms, // 객실 상세 정보
    List<String> subsidiaries // 서비스 및 부대시설 정보
) {

}
