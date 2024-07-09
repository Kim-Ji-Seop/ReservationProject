package org.boot.reservationproject.domain.facility.dto.request;



public record RoomDetail(
    String roomName, // 객실 이름
    int minPeople, // 최소 인원
    int maxPeople, // 최대 인원
    String checkInTime, // 체크인 시간
    String checkOutTime, // 체크아웃 시간
    int price // 가격
    //List<MultipartFile> roomPhotos

){}
