package org.boot.reservationproject.global;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
  CUSTOMER("CUSTOMER", "구매자"),
  SELLER("SELLER", "판매자");

  private final String key;
  private final String title;
}
