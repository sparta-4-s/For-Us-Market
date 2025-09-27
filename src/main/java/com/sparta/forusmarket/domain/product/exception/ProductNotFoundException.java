package com.sparta.forusmarket.domain.product.exception;

import com.sparta.forusmarket.common.exception.ErrorCode;
import com.sparta.forusmarket.common.exception.GlobalException;

public class ProductNotFoundException extends GlobalException {

    public ProductNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
