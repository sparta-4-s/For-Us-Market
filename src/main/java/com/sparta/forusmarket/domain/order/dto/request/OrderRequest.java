package com.sparta.forusmarket.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderRequest {
    @NotNull(message = "유저 아이디는 필수입니다.")
    private Long userId;

    @NotNull(message = "상품 아이디는 필수입니다.")
    private Long productId;

    @NotNull(message = "상품 갯수는 필수입니다.")
    private Integer quantity;

    @NotNull(message = "상품 가격은 필수입니다.")
    private BigDecimal price;

    @NotNull(message = "도시명은 필수입니다.")
    @Length(max = 50, message = "도시명은 50글자 이하로 입력해주세요.")
    private String city;

    @NotNull(message = "상세주소는 필수입니다.")
    @Length(max = 100, message = "상세주소는 100글자 이하로 입력해주세요.")
    private String street;

    @NotNull(message = "우편번호는 필수입니다.")
    @Length(max = 100, message = "우편번호는 100글자 이하로 입력해주세요.")
    private String zipcode;
}
