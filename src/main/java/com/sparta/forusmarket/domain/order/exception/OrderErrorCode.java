package com.sparta.forusmarket.domain.order.exception;

import com.sparta.forusmarket.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않은 주문입니다."),
    ORDER_FAILED(HttpStatus.PRECONDITION_FAILED, "주문이 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
