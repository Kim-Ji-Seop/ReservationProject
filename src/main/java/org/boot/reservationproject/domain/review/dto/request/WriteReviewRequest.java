package org.boot.reservationproject.domain.review.dto.request;

import java.math.BigDecimal;

public record WriteReviewRequest (
    String reviewContent,
    BigDecimal rating
){

}
