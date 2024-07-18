package org.boot.reservationproject.domain.search.dto;

import java.time.LocalDate;
import lombok.Builder;
import org.boot.reservationproject.global.BaseEntity.Status;
@Builder
public record CheckListDocDto(
    LocalDate checkInDate,
    LocalDate checkOutDate,
    Status status
) {

}
