package com.sparta.forusmarket.domain.product.dto.response;

import com.sparta.forusmarket.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductResponse {
    private final Product product;

    @Builder
    public ProductResponse(Product product) {
        this.product = product;
    }

    public static ProductResponse of(Product product) {
        return ProductResponse.builder()
                .product(product)
                .build();
    }
}
