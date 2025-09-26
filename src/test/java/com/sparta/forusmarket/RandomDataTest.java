package com.sparta.forusmarket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;

public class RandomDataTest {

    private static final Map<String, List<String>> keywordMap = new HashMap<>() {{
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

    private static final Map<String, List<String>> categories = Map.of(
            "식품 및 식료품", List.of("신선식품", "가공식품"),
            "남성 의류", List.of("상의", "하의", "아우터"),
            "여성 의류", List.of("상의", "하의", "아우터", "원피스"),
            "전기제품", List.of("생활가전", "IT/모바일"),
            "도서 & 미디어", List.of("종이책", "전자책", "음반/영상"),
            "쿠폰", List.of("식당/카페", "문화/여가", "온라인 서비스")
    );

    private static final String[] keywords = {
            "사과", "바나나", "딸기", "포도", "청바지", "티셔츠",
            "원피스", "노트북", "청소기", "책", "라면", "과자", "커피"
    };

    private final Random random = new Random();

    @Test
    void createDummyData() {
        List<Product> products = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            String mainCategory = getRandomKey(categories);
            String subCategory = getRandomValue(categories.get(mainCategory));

            // 서브카테고리에 맞는 키워드 가져오기
            List<String> possibleKeywords = keywordMap.getOrDefault(subCategory, List.of("상품"));
            String keyword = possibleKeywords.get(random.nextInt(possibleKeywords.size()));

            // 키워드 위치 랜덤
            int pos = random.nextInt(3);
            String name = switch (pos) {
                case 0 -> keyword + " " + subCategory + " 상품 " + i;
                case 1 -> "인기 " + keyword + " " + subCategory + " " + i;
                default -> subCategory + " 상품 " + i + " " + keyword;
            };

            int price = 1000 + random.nextInt(100000); // 1천 ~ 10만 원
            int stock = random.nextInt(100) + 1;       // 1~100

            products.add(new Product(name, price, stock, mainCategory, subCategory));
        }

        // 샘플 출력
        products.forEach(System.out::println);
    }

    private <T> T getRandomKey(Map<T, ?> map) {
        List<T> keys = new ArrayList<>(map.keySet());
        return keys.get(random.nextInt(keys.size()));
    }

    private <T> T getRandomValue(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    // 더미 Product 클래스
    record Product(String name, int price, int stock, String category, String subCategory) {
    }
}
