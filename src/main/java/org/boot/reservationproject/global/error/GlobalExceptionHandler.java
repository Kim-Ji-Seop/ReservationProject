package org.boot.reservationproject.global.error;

import static org.boot.reservationproject.global.error.ErrorCode.INTERNAL_SERVER_ERROR;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler(BaseException.class)
  public ResponseEntity<BaseResponse<?>> handleBaseException(BaseException e) {
    return ResponseEntity
        .status(e.getCode())
        .body(new BaseResponse<>(e));
  }
}
