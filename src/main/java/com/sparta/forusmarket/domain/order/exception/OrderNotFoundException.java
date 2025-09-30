package com.sparta.forusmarket.domain.order.exception;

import com.sparta.forusmarket.common.exception.GlobalException;

public class OrderNotFoundException extends GlobalException {
    public OrderNotFoundException() {
        super(OrderErrorCode.ORDER_NOT_FOUND);
    }
}
