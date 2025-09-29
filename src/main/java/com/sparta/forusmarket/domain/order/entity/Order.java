package com.sparta.forusmarket.domain.order.entity;

import com.sparta.forusmarket.common.entity.BaseEntity;
import com.sparta.forusmarket.domain.order.enums.OrderStatus;
import com.sparta.forusmarket.domain.product.entity.Product;
import com.sparta.forusmarket.domain.user.entity.Address;
import com.sparta.forusmarket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private BigDecimal price;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;

    @Embedded
    private Address address;

    @Builder
    private Order(
            User user,
            Product product,
            int quantity,
            BigDecimal price,
            OrderStatus orderStatus,
            Address address) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.orderStatus = orderStatus;
        this.address = address;
    }

    public static Order of(
            User user,
            Product product,
            int quantity,
            BigDecimal price,
            OrderStatus orderStatus,
            Address address) {
        return Order.builder()
                .user(user)
                .product(product)
                .quantity(quantity)
                .price(price)
                .orderStatus(orderStatus)
                .address(address)
                .build();
    }

    public void changeStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
