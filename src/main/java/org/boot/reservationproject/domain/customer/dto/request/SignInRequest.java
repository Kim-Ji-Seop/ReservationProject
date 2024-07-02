package org.boot.reservationproject.domain.customer.dto.request;

public record SignInRequest(
    String email,
    String password
) {}
