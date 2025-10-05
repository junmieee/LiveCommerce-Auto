package com.example.common.exception;

import com.example.user.presentation.dto.response.SimpleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<SimpleResponse> handleValidation(MethodArgumentNotValidException ex) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(err -> err.getDefaultMessage())
            .orElse("요청이 올바르지 않습니다.");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SimpleResponse(false, message));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<SimpleResponse> handleBadRequest(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new SimpleResponse(false, ex.getMessage()));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<SimpleResponse> handleAccessDenied(AccessDeniedException ex) {
    String message = ex.getMessage();
    if (message == null || message.isBlank()) {
      message = "접근 권한이 없습니다.";
    }
    log.warn("Access denied: {}", message);
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new SimpleResponse(false, message));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<SimpleResponse> handleServerError(Exception ex) {
    log.error("Unhandled exception", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new SimpleResponse(false, "서버 오류가 발생했습니다."));
  }
}
