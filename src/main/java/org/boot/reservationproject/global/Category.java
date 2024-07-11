package org.boot.reservationproject.global;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
  TOTAL("전체"),
  MOTEL("모텔"),
  HOTEL("호텔&리조트"),
  PENSION("펜션&풀빌라"),
  CAMPING("캠핑&글램핑");

  private final String categoryName;
}
