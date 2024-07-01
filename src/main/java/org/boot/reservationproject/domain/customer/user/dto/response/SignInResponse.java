package org.boot.reservationproject.domain.customer.user.dto.response;

import lombok.Builder;
import org.boot.reservationproject.global.jwt.TokenDto;
@Builder
public record SignInResponse(
  String nickname,
  TokenDto tokenDto
) {}
