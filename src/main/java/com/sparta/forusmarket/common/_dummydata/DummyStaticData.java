package com.sparta.forusmarket.common._dummydata;

import com.sparta.forusmarket.domain.product.type.CategoryType;
import com.sparta.forusmarket.domain.product.type.SubCategoryType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DummyStaticData {

    public static final Map<String, List<String>> keywordMap = new HashMap<>() {{
        put("신선식품", List.of("사과", "바나나", "딸기", "포도", "소고기", "삼겹살", "광어", "연어"));
        put("가공식품", List.of("라면", "과자", "주스", "커피", "김치", "즉석밥"));

        put("상의", List.of("티셔츠", "셔츠", "맨투맨", "니트"));
        put("하의", List.of("청바지", "슬랙스", "반바지"));
        put("아우터", List.of("코트", "점퍼", "재킷"));
        put("원피스", List.of("원피스", "드레스"));

        put("생활가전", List.of("청소기", "냉장고", "세탁기", "에어컨"));
        put("IT/모바일", List.of("노트북", "스마트폰", "태블릿", "스마트워치"));

        put("종이책", List.of("소설", "에세이", "교재", "잡지"));
        put("전자책", List.of("전자책", "e북", "리더기"));
        put("음반/영상", List.of("앨범", "CD", "DVD", "블루레이"));

        put("식당/카페", List.of("커피쿠폰", "빵쿠폰", "식사권"));
        put("문화/여가", List.of("영화티켓", "뮤지컬티켓", "공연티켓"));
        put("온라인 서비스", List.of("스트리밍이용권", "게임아이템쿠폰", "클라우드이용권"));
    }};

    public static final Map<CategoryType, List<SubCategoryType>> categories = Map.of(
            CategoryType.FOOD, List.of(SubCategoryType.FRESH_FOOD, SubCategoryType.PROCESSED_FOOD),

            CategoryType.MEN_CLOTHING, List.of(
                    SubCategoryType.MEN_TOP,
                    SubCategoryType.MEN_BOTTOM,
                    SubCategoryType.MEN_OUTER
            ),

            CategoryType.WOMEN_CLOTHING, List.of(
                    SubCategoryType.WOMEN_TOP,
                    SubCategoryType.WOMEN_BOTTOM,
                    SubCategoryType.WOMEN_OUTER,
                    SubCategoryType.WOMEN_DRESS
            ),

            CategoryType.ELECTRONICS, List.of(
                    SubCategoryType.HOME_APPLIANCE,
                    SubCategoryType.IT_MOBILE
            ),

            CategoryType.BOOKS_MEDIA, List.of(
                    SubCategoryType.PAPER_BOOK,
                    SubCategoryType.EBOOK,
                    SubCategoryType.MUSIC_VIDEO
            ),

            CategoryType.COUPON, List.of(
                    SubCategoryType.FOOD_CAFE_COUPON,
                    SubCategoryType.CULTURE_COUPON,
                    SubCategoryType.ONLINE_SERVICE_COUPON
            )
    );

}
