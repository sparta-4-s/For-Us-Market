package com.sparta.forusmarket.common._dummydata;

import com.sparta.forusmarket.common._dummydata.dto.ProductDto;
import com.sparta.forusmarket.common._dummydata.utils.DateUtil;
import com.sparta.forusmarket.common._dummydata.utils.RandomUtil;
import com.sparta.forusmarket.domain.product.type.CategoryType;
import com.sparta.forusmarket.domain.product.type.SubCategoryType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class ProductGenerator {

    private final Random random = new Random();

    public ProductDto generate(final int index) {
        CategoryType mainCategory = RandomUtil.getRandomKey(DummyStaticData.categories);
        SubCategoryType subCategory = RandomUtil.getRandomValue(DummyStaticData.categories.get(mainCategory));

        // 키워드 가져오기
        List<String> possibleKeywords = DummyStaticData.keywordMap
                .getOrDefault(subCategory.getDescription(), List.of("상품"));
        String keyword = RandomUtil.getRandomValue(possibleKeywords);

        // 상품명 생성
        String name = generateName(keyword, subCategory.getDescription(), index);

        BigDecimal price = BigDecimal.valueOf(1000 + random.nextInt(100000)); // 1천 ~ 10만 원
        BigDecimal discountRate = BigDecimal.valueOf(random.nextInt(50)); // 0 ~ 50%
        int stock = random.nextInt(100) + 1; // 1 ~ 100

        // 랜덤 날짜 범위: 2020-01-01 ~ 현재
        // created_at: start ~ end 사이 랜덤
        LocalDateTime createdAt = DateUtil.getRandomDateTime(
                LocalDateTime.of(2020, 1, 1, 0, 0),
                LocalDateTime.now()
        );

        // updated_at: createdAt ~ end 사이 랜덤
        LocalDateTime updatedAt = DateUtil.getRandomDateTime(createdAt, LocalDateTime.now());

        return new ProductDto(name, price, stock, mainCategory, subCategory, discountRate, createdAt, updatedAt);
    }

    private String generateName(String keyword, String subCategory, int index) {
        return switch (random.nextInt(3)) {
            case 0 -> keyword + " " + subCategory + " 상품 " + index;
            case 1 -> "인기 " + keyword + " " + subCategory + " " + index;
            default -> subCategory + " 상품 " + index + " " + keyword;
        };
    }

}
