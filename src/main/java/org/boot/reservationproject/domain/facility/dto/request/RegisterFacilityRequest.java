package org.boot.reservationproject.domain.facility.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.boot.reservationproject.global.Category;
import lombok.Builder;

@Builder
public record RegisterFacilityRequest(
    @NotBlank Category category, // 시설 카테고리
    @NotBlank String name, // 시설 이름
    @NotBlank String region, // 지역
    @NotBlank String location, // 상세주소
    @NotBlank String regCancelRefund, // 취소 및 환불 규정
    @NotNull List<RoomDetail> rooms, // 객실 상세 정보
    @NotNull List<String> subsidiaries // 서비스 및 부대시설 정보
){}
