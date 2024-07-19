package org.boot.reservationproject.domain.customer.dto.request;

public record UpdateCustomerInfoRequest(
    String nickname,
    String name
) {

}
