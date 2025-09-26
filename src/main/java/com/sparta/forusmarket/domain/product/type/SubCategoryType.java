package com.sparta.forusmarket.domain.product.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubCategoryType {

    // FOOD
    FRESH_FOOD("신선식품", CategoryType.FOOD),
    PROCESSED_FOOD("가공식품", CategoryType.FOOD),

    // MEN
    MEN_TOP("남성 상의", CategoryType.MEN_CLOTHING),
    MEN_BOTTOM("남성 하의", CategoryType.MEN_CLOTHING),
    MEN_OUTER("남성 아우터", CategoryType.MEN_CLOTHING),

    // WOMEN
    WOMEN_TOP("여성 상의", CategoryType.WOMEN_CLOTHING),
    WOMEN_BOTTOM("여성 하의", CategoryType.WOMEN_CLOTHING),
    WOMEN_OUTER("여성 아우터", CategoryType.WOMEN_CLOTHING),
    WOMEN_DRESS("여성 원피스", CategoryType.WOMEN_CLOTHING),

    // ELECTRONICS
    HOME_APPLIANCE("생활가전", CategoryType.ELECTRONICS),
    IT_MOBILE("IT/모바일", CategoryType.ELECTRONICS),

    // BOOKS_MEDIA
    PAPER_BOOK("종이책", CategoryType.BOOKS_MEDIA),
    EBOOK("전자책", CategoryType.BOOKS_MEDIA),
    MUSIC_VIDEO("음반/영상", CategoryType.BOOKS_MEDIA),

    // COUPON
    FOOD_CAFE_COUPON("식당/카페 쿠폰", CategoryType.COUPON),
    CULTURE_COUPON("문화/여가 쿠폰", CategoryType.COUPON),
    ONLINE_SERVICE_COUPON("온라인 서비스 쿠폰", CategoryType.COUPON);

    private final String description;
    private final CategoryType parent;
}
