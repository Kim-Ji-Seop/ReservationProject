package org.boot.reservationproject.domain.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record WriteReviewRequest (
    @NotBlank(message = "리뷰 내용은 필수 입력 값입니다")
    @Size(max = 500, message = "최대 500글자 입니다")
    String reviewContent,
    @NotNull BigDecimal rating
){

}
