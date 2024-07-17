package org.boot.reservationproject.domain.reservation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentRequest(
    @JsonProperty("imp_uid") String impUid,
    @JsonProperty("merchant_uid") String merchantUid,
    @JsonProperty("buyer_name") String customerName,
    @JsonProperty("buyer_phone_number") String customerPhoneNumber
) {

}
