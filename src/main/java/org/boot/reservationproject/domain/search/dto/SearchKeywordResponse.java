package org.boot.reservationproject.domain.search.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import org.boot.reservationproject.global.Category;

@Builder
public record SearchKeywordResponse (
    Long id,
    String facilityName,
    Category category,
    String region,
    String location,
    BigDecimal averageRating,
    int numberOfReviews,
    String previewFacilityPhotoUrl,
    String previewFacilityPhotoName,
    List<RoomDocsPerFacility> rooms
    ){

}
