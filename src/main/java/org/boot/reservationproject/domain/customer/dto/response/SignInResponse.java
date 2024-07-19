package org.boot.reservationproject.domain.customer.dto.response;

import lombok.Builder;
import org.boot.reservationproject.global.jwt.TokenDto;
@Builder
public record SignInResponse(
  Long userIdx,
  String nickname,
  TokenDto tokenDto
) {}
