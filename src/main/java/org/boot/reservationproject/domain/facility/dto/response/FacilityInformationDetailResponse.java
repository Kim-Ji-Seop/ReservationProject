package org.boot.reservationproject.domain.facility.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import org.boot.reservationproject.global.Category;
@Builder
public record FacilityInformationDetailResponse(
    String facilityName,
    Category category,
    String region,
    String location,
    String regCancelRefund,
    BigDecimal averageRating,
    int numberOfReviews,
    List<RoomPreviews> roomPreviewsList,
    List<String> subsidiaryDetails

) {

}
