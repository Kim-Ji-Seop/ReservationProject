package org.boot.reservationproject.domain.facility.dto.response;

import java.math.BigDecimal;
import org.boot.reservationproject.global.Category;

public record FacilitiesInformationPreviewResponse(
  Long facilityIdx,
  Category category,
  String name,
  String region,
  BigDecimal averageRating,
  int numberOfReviews,
  int price,
  String previewPhotoBase64

) {

}
