package org.boot.reservationproject.domain.reservation.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record CreateReservationRequest(
    @NotNull Long facilityId,
    @NotNull Long roomId,
    @NotNull LocalDate checkinDate,
    @NotNull LocalDate checkoutDate
){

}
