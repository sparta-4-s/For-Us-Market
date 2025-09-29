package com.sparta.forusmarket.domain.product.exception;

import com.sparta.forusmarket.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않은 상품입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
