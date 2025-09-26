package com.sparta.forusmarket.common._dummydata.dto;

import com.sparta.forusmarket.domain.product.type.CategoryType;
import com.sparta.forusmarket.domain.product.type.SubCategoryType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductDto(
        String name,
        BigDecimal price,
        int stock,
        CategoryType category,
        SubCategoryType subCategory,
        BigDecimal discountRate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
