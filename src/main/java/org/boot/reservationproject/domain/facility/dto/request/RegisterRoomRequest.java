package org.boot.reservationproject.domain.facility.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record RegisterRoomRequest(
    @NotNull RoomDetail registerRoom // 객실 상세 정보
) {

}
