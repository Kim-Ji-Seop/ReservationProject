package org.boot.reservationproject.domain.seller.manager.dto.request;

public record ManagerSignUpRequest(
    //회사 이메일, 비밀번호, 대표 전화번호, 대표 이름, 사업자 번호, 법인명, 법인주소 입력
    String cpEmail,
    String password,
    String epPhoneNumber,
    String epName,
    String epCode,
    String cpName,
    String cpLocation
) {

}
