package org.boot.reservationproject.domain.facility.dto.request;

import java.util.List;

public record RegisterRoomRequest(
    RoomDetail registerRoom // 객실 상세 정보
) {

}
