package org.boot.reservationproject.global.error;

import static org.boot.reservationproject.global.error.ErrorCode.INTERNAL_SERVER_ERROR;
import static org.boot.reservationproject.global.error.ErrorCode.INVALID_VALUE;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorResponse> handleValidationException(
      MethodArgumentNotValidException e,
      HttpServletRequest request
  ) {
    // 필드별로 발생한 오류를 수집
    Map<String, List<String>> fieldErrors = e.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.groupingBy(
            FieldError::getField,
            Collectors.mapping(
                error -> Objects.requireNonNullElse(error.getDefaultMessage(), "").trim(),
                Collectors.toList()
            )
        ));

    // 수집된 오류를 원하는 형태로 변환
    List<Map<String, String>> errors = fieldErrors.entrySet().stream()
        .map(entry -> Map.of(entry.getKey(), String.join(", ", entry.getValue())))
        .collect(Collectors.toList());

    // ValidationErrorResponse 객체 생성
    ValidationErrorResponse response =
        new ValidationErrorResponse(INVALID_VALUE.getCode(), errors);

    return ResponseEntity
        .status(INVALID_VALUE.getHttpStatus())
        .body(response);
  }

  // 이외 Error
  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponse> handleException(
      Exception e, HttpServletRequest request)
  {
    log.error("[Common Exception] url: {} | errorMessage: {}",
        request.getRequestURL(), e.getMessage());
    return ResponseEntity
        .status(INTERNAL_SERVER_ERROR.getHttpStatus())
        .body(new BaseResponse<>(INTERNAL_SERVER_ERROR));
  }
}
