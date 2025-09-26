package com.sparta.forusmarket;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

public class DataFakerTest {

    private static final Faker faker = new Faker(new Locale("ko", "KR"));

    // 카테고리 구조 정의
    private static final Map<String, List<String>> categories = Map.of(
            "식품 및 식료품", List.of("신선식품", "가공식품"),
            "남성 의류", List.of("상의", "하의", "아우터"),
            "여성 의류", List.of("상의", "하의", "아우터", "원피스"),
            "전기제품", List.of("생활가전", "IT/모바일"),
            "도서 & 미디어", List.of("종이책", "전자책", "음반/영상"),
            "쿠폰", List.of("식당/카페", "문화/여가", "온라인 서비스")
    );

    private static <T> T getRandomKey(Map<T, ?> map) {
        List<T> keys = new ArrayList<>(map.keySet());
        return keys.get(new Random().nextInt(keys.size()));
    }

    private static <T> T getRandomValue(List<T> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    @Test
    void createDummyData() {
        List<Product> products = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            // 랜덤 카테고리 / 서브카테고리 선택
            String mainCategory = getRandomKey(categories);
            String subCategory = getRandomValue(categories.get(mainCategory));
            System.out.println(faker.address().city());
            System.out.println(faker.commerce().productName());
            // 랜덤 상품명 (faker 활용)
            String productName = switch (mainCategory) {
                case "식품 및 식료품" -> faker.food().dish();
                case "남성 의류", "여성 의류" -> faker.commerce().productName();
                case "전기제품" -> faker.commerce().material() + " " + faker.commerce().productName();
                case "도서 & 미디어" -> faker.book().title();
                case "쿠폰" -> subCategory + " 할인권 " + faker.number().digit();
                default -> faker.commerce().productName();
            };

            int price = faker.number().numberBetween(1000, 100000); // 1천 ~ 10만 원
            int stock = faker.number().numberBetween(0, 500);       // 재고

            products.add(new Product(productName, price, stock, mainCategory, subCategory));
        }

        // 샘플 출력
        products.forEach(System.out::println);
    }

    // 더미 Product 클래스
    record Product(String name, int price, int stock, String category, String subCategory) {
    }
}
