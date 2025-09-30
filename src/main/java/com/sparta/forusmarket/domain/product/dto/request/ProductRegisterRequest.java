package com.sparta.forusmarket.domain.product.dto.request;

import com.sparta.forusmarket.domain.product.entity.Product;
import com.sparta.forusmarket.domain.product.type.CategoryType;
import com.sparta.forusmarket.domain.product.type.SubCategoryType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ProductRegisterRequest {

    @NotBlank(message = "상품명은 필수입니다.")
    private String name;

    @NotNull(message = "상품 가격은 필수입니다.")
    @Min(value = 1000, message = "상품 가격은 1000원 이상이여야 합니다.")
    private BigDecimal price;

    @Min(value = 10, message = "상품 재고는 10개 이상이여야 합니다.")
    private int stock;

    @NotNull(message = "할인율은 필수입니다.")
    @DecimalMin(value = "0.0", message = "할인율은 0 이상이어야 합니다.")
    @DecimalMax(value = "100.0", message = "할인율은 100 이하이어야 합니다.")
    private BigDecimal discountRate;

    @NotNull(message = "카테고리는 필수 값입니다.")
    private CategoryType category;

    @NotNull(message = "서브 카테고리는 필수 값입니다.")
    private SubCategoryType subCategory;

    public void setCategory(String category) {
        this.category = CategoryType.valueOf(category.toUpperCase());
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = SubCategoryType.valueOf(subCategory.toUpperCase());
    }

    public Product toEntity() {
        return Product.create(name, price, stock, subCategory, category, discountRate);
    }
}
