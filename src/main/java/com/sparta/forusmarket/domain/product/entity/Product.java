package com.sparta.forusmarket.domain.product.entity;

import com.sparta.forusmarket.common.entity.BaseEntity;
import com.sparta.forusmarket.domain.product.enums.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal price;

    private int stock;

    @Enumerated(value = EnumType.STRING)
    private Category category;

    private BigDecimal discountRate;

    public Product(Long id) {
        this.name = "Product ";
        this.price = BigDecimal.ZERO;
        this.stock = 5;
        this.category = Category.NONE;
        this.discountRate = BigDecimal.ZERO;
    }

    public void updateStock(int stock) {
        this.stock = stock;
    }
}
