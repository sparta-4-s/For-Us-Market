package com.sparta.forusmarket.domain.order.entity;

import com.sparta.forusmarket.common.entity.BaseEntity;
import com.sparta.forusmarket.domain.order.enums.OrderStatus;
import com.sparta.forusmarket.domain.product.entity.Product;
import com.sparta.forusmarket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @Column(length = 50)
    private String city;

    @Column(length = 100)
    private String street;

    @Column(length = 100)
    private String zipcode;

    private Order(
            User user,
            Product product,
            int quantity,
            BigDecimal price,
            OrderStatus orderStatus,
            String city,
            String street,
            String zipcode) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.orderStatus = orderStatus;
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    public static Order of(
            User user,
            Product product,
            int quantity,
            BigDecimal price,
            OrderStatus orderStatus,
            String city,
            String street,
            String zipcode) {
        return new Order(
                user,
                product,
                quantity,
                price,
                orderStatus,
                city,
                street,
                zipcode);
    }

    public void changeStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
