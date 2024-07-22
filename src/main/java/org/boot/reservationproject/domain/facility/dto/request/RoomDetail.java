package org.boot.reservationproject.domain.facility.dto.request;


import jakarta.validation.constraints.NotBlank;

public record RoomDetail(
  @NotBlank String roomName, // 객실 이름
  int minPeople, // 최소 인원
  int maxPeople, // 최대 인원
  @NotBlank String checkInTime, // 체크인 시간
  @NotBlank String checkOutTime, // 체크아웃 시간
  int price // 가격
){}
