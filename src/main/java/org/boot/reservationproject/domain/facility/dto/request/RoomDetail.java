package org.boot.reservationproject.domain.facility.dto.request;

import jakarta.annotation.Nullable;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record RoomDetail(
    String roomName, // 객실 이름
    @Nullable String roomNumber, // 객실 번호(호수)
    int minPeople, // 최소 인원
    int maxPeople, // 최대 인원
    String checkInTime, // 체크인 시간
    String checkOutTime, // 체크아웃 시간
    int price // 가격
    //List<MultipartFile> roomPhotos

){}
