package org.boot.reservationproject.domain.review.dto.response;

import java.util.List;
import org.boot.reservationproject.domain.facility.dto.response.FacilitiesPageResponse.PageMetadata;

public record ReviewsPageResponse(
  List<ReviewDetail> reviewDetailList,
  PageMetadata pageMetadata
) {

}
