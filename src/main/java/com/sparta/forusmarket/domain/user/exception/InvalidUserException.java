package com.sparta.forusmarket.domain.user.exception;

import com.sparta.forusmarket.common.exception.ErrorCode;
import com.sparta.forusmarket.common.exception.GlobalException;

public class InvalidUserException extends GlobalException {

    public InvalidUserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
