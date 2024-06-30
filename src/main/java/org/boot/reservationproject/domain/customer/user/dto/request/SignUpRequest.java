package org.boot.reservationproject.domain.customer.user.dto.request;

public record SignUpRequest(
    String email,
    String password,
    String phoneNumber,
    String birthday,
    String gender,
    String nickname
) {}
