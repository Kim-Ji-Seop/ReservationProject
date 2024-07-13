package org.boot.reservationproject.domain.facility.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
public record FacilitiesPageResponse(
    List<FacilitiesInformationPreviewResponse> content,
    PageMetadata pageMetadata
) {
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PageMetadata {
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private boolean first;
    private int numberOfElements;
    private boolean empty;
  }
}
