package com.sparta.forusmarket.domain.order.dto.response;

import com.sparta.forusmarket.domain.order.entity.Order;
import com.sparta.forusmarket.domain.order.enums.OrderStatus;
import com.sparta.forusmarket.domain.user.dto.AddressDto;
import lombok.AccessLevel;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder(access = AccessLevel.PRIVATE)
public record OrderResponse(
        Long id,
        Long userId,
        Long productId,
        Integer quantity,
        BigDecimal price,
        OrderStatus status,
        AddressDto address,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    @Builder
    public static OrderResponse of(
            Long id,
            Long userId,
            Long productId,
            Integer quantity,
            BigDecimal price,
            OrderStatus status,
            AddressDto address,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return OrderResponse.builder()
                .id(id)
                .userId(userId)
                .productId(productId)
                .quantity(quantity)
                .price(price)
                .status(status)
                .address(address)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .productId(order.getProduct().getId())
                .quantity(order.getQuantity())
                .price(order.getPrice())
                .status(order.getOrderStatus())
                .address(AddressDto.from(order.getAddress()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
