package org.boot.reservationproject.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
  // 고정 코드
  SUCCESS(HttpStatus.OK,200,  "요청에 성공하였습니다."),
  BAD_REQUEST( HttpStatus.BAD_REQUEST,400,  "입력값을 확인해주세요."),
  FORBIDDEN(HttpStatus.FORBIDDEN,  403,"권한이 없습니다."),
  NOT_FOUND(HttpStatus.NOT_FOUND, 404,"대상을 찾을 수 없습니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,500,"서버 내부에 오류가 발생했습니다."),

  // jwt
  TOKEN_NOT_EXIST(HttpStatus.UNAUTHORIZED, 401, "JWT Token이 존재하지 않습니다."),

  // Validation
  INVALID_VALUE(HttpStatus.BAD_REQUEST, 400, "잘못된 입력값입니다."),
  USER_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR,500,"유저를 찾지 못했습니다.");


  private final HttpStatus httpStatus;
  private final int code;
  private final String message;
}
