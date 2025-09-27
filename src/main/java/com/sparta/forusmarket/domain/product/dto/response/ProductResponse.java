package com.sparta.forusmarket.domain.product.dto.response;

import com.sparta.forusmarket.domain.product.entity.Product;
import com.sparta.forusmarket.domain.product.type.CategoryType;
import com.sparta.forusmarket.domain.product.type.SubCategoryType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        int stock,
        SubCategoryType subCategory,
        CategoryType category,
        BigDecimal discountRate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ProductResponse of(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .subCategory(product.getSubCategory())
                .category(product.getCategory())
                .discountRate(product.getDiscountRate())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
