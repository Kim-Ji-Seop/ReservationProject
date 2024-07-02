package org.boot.reservationproject.global.error;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationErrorResponse {
  private int code;
  private List<Map<String, String>> message;
}
