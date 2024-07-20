package org.boot.reservationproject.domain.review.dto.response;

import java.math.BigDecimal;

public record ReviewDetail(
    Long reviewIdx,
    String nickname,
    BigDecimal rating,
    String createdAt, // 1분 전, 1개월 전, 1주 전 ..
    String roomName,
    String content
) {}
