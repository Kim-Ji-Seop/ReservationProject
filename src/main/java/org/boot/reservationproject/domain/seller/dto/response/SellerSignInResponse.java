package org.boot.reservationproject.domain.seller.dto.response;

import lombok.Builder;
import org.boot.reservationproject.global.jwt.TokenDto;
@Builder
public record SellerSignInResponse(
    String epName,
    String cpName,
    TokenDto tokenDto
) {

}
