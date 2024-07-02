package org.boot.reservationproject.domain.seller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SellerSignUpRequest(
    //회사 이메일, 비밀번호, 대표 전화번호, 대표 이름, 사업자 번호, 법인명, 법인주소 입력
    @NotBlank(message = "회사 이메일은 필수 입력 값입니다")
    @Email(message = "유효한 이메일 주소를 입력해주세요")
    @Size(max = 300, message = "이메일 주소는 300자 이내여야 합니다")
    String cpEmail,

    @NotBlank(message = "비밀번호는 필수 입력 값입니다")
    @Size(min = 8, max = 60, message = "비밀번호는 8자 이상 60자 이내여야 합니다.")
    String password,

    @NotBlank(message = "대표 전화번호는 필수 입력 값입니다")
    @Size(max = 11, message = "전화번호는 11자 이내여야 합니다")
    String epPhoneNumber,

    @NotBlank(message = "대표 이름은 필수 입력 값입니다")
    @Size(max = 10, message = "대표 이름은 10자 이내여야 합니다")
    String epName,

    @NotBlank(message = "사업자 번호는 필수 입력 값입니다")
    @Size(max = 20, message = "사업자 번호는 20자 이내여야 합니다")
    String epCode,

    @NotBlank(message = "법인명은 필수 입력 값입니다")
    @Size(max = 50, message = "법인명은 50자 이내여야 합니다")
    String cpName,

    @NotBlank(message = "법인 주소는 필수 입력 값입니다")
    @Size(max = 100, message = "법인 주소는 100자 이내여야 합니다")
    String cpLocation
) {

}
