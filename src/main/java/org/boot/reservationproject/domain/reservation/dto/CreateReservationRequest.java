package org.boot.reservationproject.domain.reservation.dto;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record CreateReservationRequest(
    Long facilityId,
    Long roomId,
    LocalDate checkinDate,
    LocalDate checkoutDate
){

}
