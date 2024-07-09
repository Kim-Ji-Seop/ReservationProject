package org.boot.reservationproject.domain.facility.dto.response;

public record RoomPreviews(
    Long roomIdx,
    int minPeople,
    int maxPeople,
    String checkInTime,
    String checkOutTime,
    int price,
    String thumbNailPhoto // Base64 인코딩 String
) {

}
