package com.sparta.forusmarket.domain.log.entity;

import com.sparta.forusmarket.common.entity.BaseEntity;
import com.sparta.forusmarket.domain.log.enums.LogStatus;
import jakarta.persistence.*;
        import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "order_log")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderLog{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    // FK들을 전부 ID 값으로만 보관
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "product_id")
    private Long productId;

    private Integer quantity;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogStatus status; // SUCCESS / FAILUIRE / CANCLED

    @Column(length = 255)
    private String message; // 실패 사유

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Builder(access = AccessLevel.PRIVATE)
    private OrderLog(Long orderId,
                     Long userId,
                     Long productId,
                     Integer quantity,
                     BigDecimal price,
                     LogStatus status,
                     String message) {
        this.orderId = orderId;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.message = message;
    }

    public static OrderLog success(Long orderId,
                                   Long userId,
                                   Long productId,
                                   Integer quantity,
                                   BigDecimal price) {
        return OrderLog.builder()
                .orderId(orderId)
                .userId(userId)
                .productId(productId)
                .quantity(quantity)
                .price(price)
                .status(LogStatus.SUCCESS)
                .build();
    }

    public static OrderLog fail(String message) {
        return OrderLog.builder()
                .status(LogStatus.FAILURE)
                .message(message)
                .build();
    }

    public static OrderLog cancel(Long orderId,
                                Long userId,
                                Long productId,
                                Integer quantity,
                                BigDecimal price,
                                String message) {
        return OrderLog.builder()
                .orderId(orderId)
                .userId(userId)
                .productId(productId)
                .quantity(quantity)
                .price(price)
                .status(LogStatus.CANCELED)
                .message(message)
                .build();
    }
}
