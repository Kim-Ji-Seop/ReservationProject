package org.boot.reservationproject.domain.review.dto.response;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ReviewRatingResponse(
    BigDecimal rating,
    int numberOfReviews
) {

}
