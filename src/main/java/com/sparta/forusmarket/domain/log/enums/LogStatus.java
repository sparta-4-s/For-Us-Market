package com.sparta.forusmarket.domain.log.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum LogStatus {
    SUCCESS,    // 주문 성공
    FAILURE,    // 주문 실패
    CANCELED    // 주문 취소
}
