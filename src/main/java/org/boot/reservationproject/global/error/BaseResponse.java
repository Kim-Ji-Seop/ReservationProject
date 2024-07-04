package org.boot.reservationproject.global.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.boot.reservationproject.global.error.ErrorCode.SUCCESS;
@Getter
@AllArgsConstructor
@JsonPropertyOrder({"code", "message", "result"})
public class BaseResponse<T> {

  private final String message;

  private final int code;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private T result;

  //성공 시
  public BaseResponse(T result) {
    this.code = SUCCESS.getCode(); // 성공 코드 : 200
    this.message = SUCCESS.getMessage(); // 성공 메세지
    this.result = result; // JSON 데이터
  }
  // 예외발생 시
  public BaseResponse(BaseException e) {
    this.code=e.getCode();
    this.message=e.getMessage();
  }
}