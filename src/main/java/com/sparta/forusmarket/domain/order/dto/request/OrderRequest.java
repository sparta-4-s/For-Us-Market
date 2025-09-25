package com.sparta.forusmarket.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OrderRequest {
    @NotNull(message = "유저 아이디는 필수입니다.")
    private Long userId;

    @NotNull(message = "상품 아이디는 필수입니다.")
    private Long productId;

    @NotNull(message = "상품 갯수는 필수입니다.")
    private Integer quantity;

    @NotNull(message = "상품 가격은 필수입니다.")
    private BigDecimal price;
}
