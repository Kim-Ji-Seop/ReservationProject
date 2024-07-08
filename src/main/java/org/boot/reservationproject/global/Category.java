package org.boot.reservationproject.global;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
  MOTEL("모텔"),
  HOTEL("호텔"),
  RESORT("리조트"),
  PENSION("펜션"),
  POOL_VILLA("풀빌라"),
  CAMPING("캠핑"),
  GLAMPING("글램핑");

  private final String categoryName;
}
