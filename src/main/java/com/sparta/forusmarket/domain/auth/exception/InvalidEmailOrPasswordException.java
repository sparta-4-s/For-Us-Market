package com.sparta.forusmarket.domain.auth.exception;

import com.sparta.forusmarket.common.exception.ErrorCode;
import com.sparta.forusmarket.common.exception.GlobalException;

public class InvalidEmailOrPasswordException extends GlobalException {
    public InvalidEmailOrPasswordException(ErrorCode errorCode) {
        super(errorCode);
    }
}
