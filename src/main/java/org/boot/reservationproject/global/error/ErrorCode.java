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
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED,  401,"유효하지 않은 JWT Token 입니다."),

  // Validation
  INVALID_VALUE(HttpStatus.BAD_REQUEST, 400, "잘못된 입력값입니다."),
  USER_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR,500,"유저를 찾지 못했습니다."),
  FACILITY_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR,500,"시설을 찾지 못했습니다."),
  ROOM_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR,500,"객실을 찾지 못했습니다."),

  // Reservation Failed
  RESERVATION_FAILED(HttpStatus.BAD_REQUEST,400,"해당 일자에는 예약할 수 없습니다."),

  // Payment Exception
  RESERVATION_NOT_FOUND_BY_MID(HttpStatus.INTERNAL_SERVER_ERROR,500,"예약서버에서 문제가 생겼습니다."),

  // File
  FILE_MAX_SIZE_OVER(HttpStatus.BAD_REQUEST,400,"파일 크기가 너무 큽니다.");



  private final HttpStatus httpStatus;
  private final int code;
  private final String message;
}
