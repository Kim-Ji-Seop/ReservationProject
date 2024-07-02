package org.boot.reservationproject.domain.customer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.boot.reservationproject.global.Gender;

public record SignUpRequest(
    @NotBlank(message = "이메일은 빈 값일 수 없습니다")
    @Email(message = "이메일 형식으로 되어있어야 합니다")
    @Size(max = 300, message = "이메일은 최대 300글자 입니다")
    String email,

    @NotBlank(message = "비밀번호는 빈 값일 수 없습니다")
    @Size(min = 8, max = 60, message = "최소 8글자 이상, 최대 60글자 이하를 입력해주세요.")
    String password,

    @NotBlank(message = "전화번호는 빈 값일 수 없습니다")
    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10~11자리여야 합니다")
    @Size(max = 11, message = "전화번호는 최대 11글자 입니다")
    String phoneNumber,

    @NotBlank(message = "생년월일은 빈 값일 수 없습니다")
    @Pattern(regexp = "^\\d{8}$", message = "생년월일은 8자리여야 합니다(yyyymmdd)")
    @Size(max = 8, message = "생년월일은 최대 8글자 입니다")
    String birthday,

    @NotNull(message = "성별은 빈 값일 수 없습니다")
    Gender gender,

    @NotBlank(message = "닉네임은 빈 값일 수 없습니다")
    @Size(max = 20, message = "닉네임은 최대 20글자 입니다")
    String nickname
) {}
