package org.boot.reservationproject.domain.facility.dto.response;

import java.math.BigDecimal;
import lombok.Builder;
import org.boot.reservationproject.global.Category;

@Builder
public record FacilitiesInformationPreviewResponse(
  Long facilityIdx,
  Category category,
  String name,
  String region,
  BigDecimal averageRating,
  int numberOfReviews,
  int price,
  String previewPhoto // Base64 인코딩 String

) {

}
