package com.sparta.forusmarket.domain.auth.exception;

import com.sparta.forusmarket.common.exception.ErrorCode;
import com.sparta.forusmarket.common.exception.GlobalException;

public class DuplicateEmailException extends GlobalException {

    public DuplicateEmailException(ErrorCode errorCode) {
        super(errorCode);
    }
}
