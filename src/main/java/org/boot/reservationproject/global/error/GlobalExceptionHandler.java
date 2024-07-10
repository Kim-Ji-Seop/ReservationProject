package org.boot.reservationproject.global.error;

import static org.boot.reservationproject.global.error.ErrorCode.FILE_MAX_SIZE_OVER;
import static org.boot.reservationproject.global.error.ErrorCode.INTERNAL_SERVER_ERROR;
import static org.boot.reservationproject.global.error.ErrorCode.INVALID_VALUE;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<BaseResponse<?>> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException e, HttpServletRequest request) {
    log.error("[MissingServletRequestParameterException Exception] url: {}", request.getRequestURL(), e);
    return ResponseEntity.badRequest().body(new BaseResponse<>(ErrorCode.BAD_REQUEST));
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class) // 파일 업로드 에러
  public ResponseEntity<BaseResponse<?>> handleMaxSizeException(MaxUploadSizeExceededException e) {
    log.error("최대 허용 크기 : {} Bytes",e.getMaxUploadSize());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new BaseResponse<>(FILE_MAX_SIZE_OVER));
  }
  // 이외 Error
  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponse<?>> handleException(
      Exception e, HttpServletRequest request)
  {
    log.error("[Common Exception] url: {}", request.getRequestURL(), e);
    return ResponseEntity
        .status(INTERNAL_SERVER_ERROR.getHttpStatus())
        .body(new BaseResponse<>(INTERNAL_SERVER_ERROR));
  }
}
