package org.boot.reservationproject.domain.customer.user.dto.request;

public record SignInRequest(
    String email,
    String password
) {}
