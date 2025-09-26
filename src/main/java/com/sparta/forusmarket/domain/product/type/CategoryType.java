package com.sparta.forusmarket.domain.product.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryType {

    FOOD("식품 및 식료품"),
    MEN_CLOTHING("남성 의류"),
    WOMEN_CLOTHING("여성 의류"),
    ELECTRONICS("전기제품"),
    BOOKS_MEDIA("도서 & 미디어"),
    COUPON("쿠폰");

    private final String description;
}
