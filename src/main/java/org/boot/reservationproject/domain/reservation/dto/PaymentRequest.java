package org.boot.reservationproject.domain.reservation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
    @JsonProperty("imp_uid") @NotBlank String impUid,
    @JsonProperty("merchant_uid") @NotBlank String merchantUid,
    @JsonProperty("buyer_name") @NotBlank String customerName,
    @JsonProperty("buyer_phone_number") @NotBlank String customerPhoneNumber
) {

}
