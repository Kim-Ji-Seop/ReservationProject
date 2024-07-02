package org.boot.reservationproject.domain.seller.dto.response;

import org.boot.reservationproject.global.jwt.TokenDto;

public record SellerSignInResponse(
    String epName,
    String cpName,
    TokenDto tokenDto
) {

}
