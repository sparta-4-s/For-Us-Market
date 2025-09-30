package com.sparta.forusmarket.domain.order.exception;

import com.sparta.forusmarket.common.exception.GlobalException;

public class OrderFailedException extends GlobalException {
    public OrderFailedException() {
        super(OrderErrorCode.ORDER_FAILED);
    }
}
