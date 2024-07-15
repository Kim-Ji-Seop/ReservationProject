package org.boot.reservationproject.domain.search.dto;

import java.math.BigDecimal;
import lombok.Builder;
import org.boot.reservationproject.global.Category;

@Builder
public record SearchKeywordResponse (
    Long id,
    String facilityName,
    Category category,
    String region,
    String location,
    String regCancelRefund,
    BigDecimal averageRating,
    int numberOfReviews,
    String previewFacilityPhotoUrl,
    String previewFacilityPhotoName
    ){

}
