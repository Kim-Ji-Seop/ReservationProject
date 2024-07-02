package org.boot.reservationproject.domain.seller.dto.request;

public record SellerSignInRequest(
    String cpEmail,
    String cpPassword
) {}
