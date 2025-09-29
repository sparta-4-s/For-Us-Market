package com.sparta.forusmarket.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Getter
@Builder
public class OrderRequest {
    @NotNull(message = "유저 아이디는 필수입니다.")
    private Long userId;

    @NotNull(message = "상품 아이디는 필수입니다.")
    private Long productId;

    @NotNull(message = "상품 갯수는 필수입니다.")
    private Integer quantity;

    @NotNull(message = "상품 가격은 필수입니다.")
    private BigDecimal price;

    @Length(max = 50)
    @NotNull(message = "도시명은 필수입니다.")
    private String city;

    @NotNull(message = "상세주소는 필수입니다.")
    @Length(max = 100)
    private String street;

    @NotNull(message = "우편번호는 필수입니다.")
    @Length(max = 100)
    private String zipcode;
}
