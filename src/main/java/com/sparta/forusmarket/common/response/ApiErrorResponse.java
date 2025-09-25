package com.sparta.forusmarket.common.response;

import com.sparta.forusmarket.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiErrorResponse {

    private final ApiErrorBody error;

    @Getter
    @Builder
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class ApiErrorBody {
        private final String status;
        private final String code;
        private final String message;
        private final String requestUrl;
        private final LocalDateTime timestamp;
    }

    public static ApiErrorResponse from(HttpStatus httpStatus, String message, HttpServletRequest request) {
        return new ApiErrorResponse(
                buildApiErrorBody(httpStatus, httpStatus.name(), message, request)
        );
    }

    public static ApiErrorResponse from(ErrorCode errorCode, HttpServletRequest request) {
        return new ApiErrorResponse(
                buildApiErrorBody(errorCode.getHttpStatus(), errorCode.name(), errorCode.getMessage(), request)
        );
    }

    private static ApiErrorBody buildApiErrorBody(HttpStatus httpStatus, String errorCodeName, String message, HttpServletRequest request) {
        return ApiErrorBody.builder()
                .status(httpStatus.toString())
                .code(errorCodeName)
                .message(message)
                .requestUrl(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
