package com.sparta.forusmarket.common.security.exception;

import com.sparta.forusmarket.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SecurityErrorCode implements ErrorCode {

    INVALID_HEADER(HttpStatus.UNAUTHORIZED, "인증 토큰이 필요합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
