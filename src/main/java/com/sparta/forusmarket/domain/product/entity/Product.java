package com.sparta.forusmarket.domain.product.entity;

import com.sparta.forusmarket.common.entity.BaseEntity;
import com.sparta.forusmarket.domain.product.type.CategoryType;
import com.sparta.forusmarket.domain.product.type.SubCategoryType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal price;

    private int stock;

    @Enumerated(value = EnumType.STRING)
    private SubCategoryType subCategory;

    @Enumerated(value = EnumType.STRING)
    private CategoryType category;

    private BigDecimal discountRate;

    @Builder
    private Product(String name, BigDecimal price, int stock, SubCategoryType subCategory, CategoryType category,
                    BigDecimal discountRate) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.subCategory = subCategory;
        this.category = category;
        this.discountRate = discountRate;
    }

    public static Product create(String name, BigDecimal price, int stock, SubCategoryType subCategory,
                                 CategoryType category,
                                 BigDecimal discountRate) {
        return Product.builder()
                .name(name)
                .price(price)
                .stock(stock)
                .category(category)
                .subCategory(subCategory)
                .discountRate(discountRate)
                .build();
    }

    public void updateAll(String name, BigDecimal price, int stock, BigDecimal discountRate, CategoryType category,
                          SubCategoryType subCategory) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.discountRate = discountRate;
        this.category = category;
        this.subCategory = subCategory;
    }

    public void reduceStock(int quantity) {
        if (this.stock < quantity)
            throw new IllegalArgumentException("Out of stock");

        this.stock -= quantity;
    }
}
