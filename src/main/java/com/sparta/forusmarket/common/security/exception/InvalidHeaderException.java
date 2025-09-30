package com.sparta.forusmarket.common.security.exception;

import com.sparta.forusmarket.common.exception.ErrorCode;
import com.sparta.forusmarket.common.exception.GlobalException;

public class InvalidHeaderException extends GlobalException {

    public InvalidHeaderException(ErrorCode errorCode) {
        super(errorCode);
    }
}
