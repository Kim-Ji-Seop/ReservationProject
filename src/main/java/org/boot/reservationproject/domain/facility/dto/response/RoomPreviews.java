package org.boot.reservationproject.domain.facility.dto.response;

public record RoomPreviews(
    Long roomIdx,
    String roomName,
    int minPeople,
    int maxPeople,
    String checkInTime,
    String checkOutTime,
    int price,
    String thumbNailPhoto
) {

}
