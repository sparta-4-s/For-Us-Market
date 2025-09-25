package com.sparta.forusmarket.domain.user.exception;

import com.sparta.forusmarket.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    INVALID_USER(HttpStatus.BAD_REQUEST, "존재하지 않은 회원입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
