package org.boot.reservationproject.domain.seller.manager.dto.request;

public record SellerSignInRequest(
    String cpEmail,
    String cpPassword
) {}
