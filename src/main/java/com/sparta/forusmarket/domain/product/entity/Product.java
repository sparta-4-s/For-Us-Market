package com.sparta.forusmarket.domain.product.entity;

import com.sparta.forusmarket.common.entity.BaseEntity;
import com.sparta.forusmarket.domain.product.type.CategoryType;
import com.sparta.forusmarket.domain.product.type.SubCategoryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
    @Version
    private int stock;

    @Enumerated(value = EnumType.STRING)
    private SubCategoryType subCategory;

    @Enumerated(value = EnumType.STRING)
    private CategoryType category;

    private BigDecimal discountRate;

    public Product(int stock) {
        this.name = "Product ";
        this.price = BigDecimal.ZERO;
        this.stock = stock;
        this.category = CategoryType.BOOKS_MEDIA;
        this.discountRate = BigDecimal.ZERO;
    }

    public void increaseStock(int stock) {
        this.stock += stock;
    }
}
