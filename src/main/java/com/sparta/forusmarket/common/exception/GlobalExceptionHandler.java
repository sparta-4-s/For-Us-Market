package com.sparta.forusmarket.common.exception;

import com.sparta.forusmarket.common.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                                  HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String defaultErrorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        return handleExceptionInternal(status, defaultErrorMessage, request);
    }

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(GlobalException ex, HttpServletRequest request) {
        log.error("비즈니스 오류 발생 ", ex);
        return handleExceptionInternal(ex.getErrorCode(), request);
    }

    private ResponseEntity<ApiErrorResponse> handleExceptionInternal(ErrorCode errorCode, HttpServletRequest request) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiErrorResponse.from(errorCode, request));
    }

    private ResponseEntity<ApiErrorResponse> handleExceptionInternal(HttpStatus httpStatus, String message, HttpServletRequest request) {
        return ResponseEntity
                .status(httpStatus)
                .body(ApiErrorResponse.from(httpStatus, message, request));
    }
}
