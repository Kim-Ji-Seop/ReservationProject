package org.boot.reservationproject.domain.facility.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record RegisterFacilityResponse(
    Long facilityIdx,
    List<RegisteredRoom> registeredRooms
) {

}
