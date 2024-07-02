package org.boot.reservationproject.domain.seller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SellerSignInRequest(
    @NotBlank(message = "회사 이메일은 필수 입력 값입니다")
    @Email(message = "이메일 형식으로 되어있어야 합니다")
    @Size(max = 300, message = "회사 이메일은 최대 300글자 입니다")
    String cpEmail,
    @NotBlank(message = "비밀번호는 필수 입력 값입니다")
    @Size(min = 8, max = 60, message = "최소 8글자 이상, 최대 60글자 이하를 입력해주세요")
    String cpPassword
) {}
