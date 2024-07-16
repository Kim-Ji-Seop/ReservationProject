package org.boot.reservationproject.domain.search.dto;

import lombok.Builder;
import org.boot.reservationproject.global.BaseEntity.Status;
@Builder
public record RoomDocsPerFacility(
    Long roomIdx,
    String roomName,
    String checkInTime,
    String checkOutTime,
    int minPeople,
    int maxPeople,
    int price,
    Status status
) {

}
