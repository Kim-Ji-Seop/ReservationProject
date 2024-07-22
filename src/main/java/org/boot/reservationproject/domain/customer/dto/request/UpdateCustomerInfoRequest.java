package org.boot.reservationproject.domain.customer.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateCustomerInfoRequest(
    @NotBlank String nickname,
    @NotBlank String name
) {

}
